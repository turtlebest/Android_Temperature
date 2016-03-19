package nyu.cs.jessicahuang.temperaturec;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import nyu.cs.jessicahuang.temperaturec.Controller.NdkJniUtils;
import nyu.cs.jessicahuang.temperaturec.Controller.AmbientSensor;
import nyu.cs.jessicahuang.temperaturec.Model.Day;

public class MainActivity extends ActionBarActivity {
    private final String logId = "MainActivity";

    // for UI
    private TextView ambientTemperatureText;
    private ListView dateTemperatureList;
    private Switch temperatureFormatSwitch;

    // for temperature info
    private boolean isCelsius = true;
    private List<Day> dayInfoList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> dateTemperatureListContent
            = new ArrayList<HashMap<String, Object>>();

    // for NDK, jni native code in C
    private NdkJniUtils jni = new NdkJniUtils();

    // for ambient sensor
    Float ambientTemperature = (float) 0.0;
    private AmbientSensor amSensor;
    public boolean isAmbientSensor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(logId, "The app started");

        dateTemperatureList = (ListView) findViewById(R.id.temperatureList);
        ambientTemperatureText = (TextView) findViewById(R.id.ambientTemperatureTitle);

        temperatureFormatSwitch = (Switch) findViewById(R.id.temperatureFormatSwitch);
        // setup the switch widgets listener when user change the switch
        temperatureFormatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("Fahrenheit");
                    switchDateTemperatureListUnit(true);
                } else {
                    buttonView.setText("Celsius");
                    switchDateTemperatureListUnit(false);
                }
                if (isAmbientSensor)
                  setAmbientTextViewValue(ambientTemperature);
            }
        });

        // update the daily information
        dayInfoList = genDayInfoList();
        if (!dayInfoList.isEmpty())
            updateDateTemperatureList(dayInfoList);

        // set the ambient sensor device
        amSensor = new AmbientSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE), this);
    }

    /**
     * generate the list of random data for daily information
     *
     */
    private List<Day> genDayInfoList() {
        Log.i(logId, "genDayInfoList");
        Random random = new Random();
        Day dayInfo[] = new Day[5];
        List<Day> list = new ArrayList<>();

        dayInfo[0] = new Day("Mon", random.nextFloat() * 30 + 10);
        dayInfo[1] = new Day("Tue", random.nextFloat() * 30 + 10);
        dayInfo[2] = new Day("Wed", random.nextFloat() * 30 + 10);
        dayInfo[3] = new Day("Thu", random.nextFloat() * 30 + 10);
        dayInfo[4] = new Day("Fri", random.nextFloat() * 30 + 10);

        list.addAll(Arrays.asList(dayInfo));

        return list;
    }
    
    /**
     * update the weather display for the list view
     * @param dayInfoList a list of Daily information object to display
     */
    private void updateDateTemperatureList(List<Day> dayInfoList) {
        Log.i(logId, "update daily information of the temperature");
        // clean the list
        dateTemperatureListContent.clear();

        // Iterator over the input and add item into dateTemperatureListContent
        Iterator dayIterator = dayInfoList.iterator();
        while (dayIterator.hasNext()) {
            Day curDay = (Day) dayIterator.next();
            String dayOfTheWeek = curDay.getDayOfTheWeek();
            Float temperature = curDay.getTemperature();

            // display one number of decimal
            temperature = (float) (Math.round(temperature * 10) / 10.0);
            curDay.setTemperature(temperature);

            String temperatureString = temperature.toString();

            HashMap<String, Object> itemInList = new HashMap<String, Object>();
            itemInList.put("dayOfTheWeek", dayOfTheWeek);
            itemInList.put("temperature", temperatureString);

            dateTemperatureListContent.add(itemInList);
        }

        //Display the list of view for temperature by SimpleAdapter
        SimpleAdapter listAdapter = new SimpleAdapter(
                MainActivity.this,
                dateTemperatureListContent,
                R.layout.listview_date_temperature,
                new String[]{"dayOfTheWeek", "temperature"},
                new int[]{R.id.dayOfTheWeekTextView, R.id.temperatureTextView}) {
            @Override
            public View getView(int index, View dayListView, ViewGroup parent) {
                dayListView = LinearLayout.inflate(
                        getBaseContext(), R.layout.listview_date_temperature, null);

                TextView dateText = (TextView) dayListView.findViewById(R.id.dayOfTheWeekTextView);
                TextView temperatureText = (TextView) dayListView.findViewById(R.id.temperatureTextView);

                dateText.setText((String)((Map<String, Object>) getItem(index)).get("dayOfTheWeek"));
                temperatureText.setText((String)((Map<String, Object>) getItem(index)).get("temperature"));

                return dayListView;
            }
        };

        dateTemperatureList.setAdapter(listAdapter);

    }

    /**
     * switch the temperature format
     *
     * @param isFahrenheit if true, switch the mode to Fahrenheit
     *                     if false, switch the mode to Celsius
     */
    private void switchDateTemperatureListUnit(boolean isFahrenheit) {
        Log.i(logId, "switchDateTemperatureListUnit");
        // Don't do conversion, it is already Celsius mode
        if (isCelsius == !isFahrenheit)
            return;

        isCelsius = !isFahrenheit;
        if (isCelsius)
            // convert the list to Celsius
            jni.convertDayListToCel(dayInfoList);
        else
            // convert the list to Fahrenheit
            jni.convertDayListToFar(dayInfoList);

        if (!dayInfoList.isEmpty())
            updateDateTemperatureList(dayInfoList);
    }

    /**
     * update the current temperature from AmbientSensor
     *
     * @param value current temperature
     *
     */
    public void setAmbientTextViewValue(float value){
        Log.i(logId, "setAmbientTextViewValue");
        ambientTemperature = value;
        if (!isCelsius)
            //transfer the value to Fahrenheit, if this is not Celsius mode
            ambientTemperature = jni.celToFar(ambientTemperature);
        ambientTemperature = (float) (Math.round(ambientTemperature * 10) / 10.0);
        ambientTemperatureText.setText(ambientTemperature.toString());
    }

    /**
     * update if there is AmbientSensor in the device
     *
     * @param isExisted the sensor is existed or not
     *
     */
    public void setAmbientSensorState(boolean isExisted){
        Log.i(logId, "setAmbientSensorState");
        if (isExisted) {
            isAmbientSensor = true;
        } else {
            isAmbientSensor = false;
            ambientTemperatureText.setText("No ambient temperature support in this mobile");
        }
    }

    @Override
    protected void onResume() {
        Log.i(logId, "onResume");
        super.onResume();

        if (isAmbientSensor) {
            amSensor.onResume();
        }

        if (!dayInfoList.isEmpty())
            updateDateTemperatureList(dayInfoList);
    }

    @Override
    protected void onPause() {
        Log.i(logId, "onPause");
        super.onPause();

        if (isAmbientSensor) {
            amSensor.onPause();
        }
    }
}
