package io.github.zhekos.compareorm;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.zhekos.compareorm.dbflow.DBFlowTester;
import io.github.zhekos.compareorm.events.LogTestDataEvent;
import io.github.zhekos.compareorm.events.TrialCompletedEvent;
import io.github.zhekos.compareorm.greendao.GreenDaoTester;
import io.github.zhekos.compareorm.ormlite.OrmLiteTester;
import io.github.zhekos.compareorm.realm.RealmTester;
import io.github.zhekos.compareorm.sql.SqlTester;

public class MainActivity extends AppCompatActivity {

    public static final String LOAD_TIME = "Load";
    public static final String SAVE_TIME = "Save";

    public static final int SIMPLE_LOOP_COUNT  = 10000;
    public static final int COMPLEX_LOOP_COUNT = 50;

    private static final String STATE_MAPDATA       = "mapData";
    private static final String STATE_RUNNING_TESTS = "runningTests";
    private static final String STATE_TEST_NAME     = "testName";

    private Button   simpleTrialButton;
    private Button   complexTrialButton;
    private Button   complexTrialButtonRead;
    private Button simpleTrialButtonRead;
    private TextView resultsLabel;
    private TextView resultsTextView;
    private static StringBuilder resultsStringBuilder = new StringBuilder();
    private static ProgressBar progressBar;
    private static CardView progressCardView;

    private BarChart chartView;
    private LinkedHashMap<String, ArrayList<BarEntry>> chartEntrySets = new LinkedHashMap<>();
    private boolean                                    runningTests   = false;
    private String runningTestName;


    private List<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        simpleTrialButton = (Button) findViewById(R.id.simple);
        complexTrialButton = (Button) findViewById(R.id.complex);
        simpleTrialButtonRead = (Button) findViewById(R.id.simpleread);
        complexTrialButtonRead = (Button) findViewById(R.id.complexread);
        resultsLabel = (TextView) findViewById(R.id.resultsLabel);
        resultsTextView = (TextView) findViewById(R.id.results);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setIndeterminate(true);
        progressCardView = (CardView) findViewById(R.id.fcv);
        chartView = (BarChart) findViewById(R.id.chart);

        if (savedInstanceState != null) {
            runningTests = savedInstanceState.getBoolean(STATE_RUNNING_TESTS);
            runningTestName = savedInstanceState.getString(STATE_TEST_NAME);
            chartEntrySets = (LinkedHashMap<String, ArrayList<BarEntry>>) savedInstanceState.getSerializable(STATE_MAPDATA);

            setBusyUI(runningTests, runningTestName);
            if (!runningTests && (chartEntrySets.size() > 0)) {
                // graph existing data
                initChart();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_RUNNING_TESTS, runningTests);
        savedInstanceState.putString(STATE_TEST_NAME, runningTestName);
        savedInstanceState.putSerializable(STATE_MAPDATA, chartEntrySets);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // handle data collection event
    public void onEvent(LogTestDataEvent event) {
        logTime(event.getStartTime(), event.getEndTime(), event.getFramework(), event.getEventName());
    }

    // handle graphing event
    public void onEventMainThread(TrialCompletedEvent event) {
        progressBar.setVisibility(View.GONE);
        progressCardView.setVisibility(View.GONE);
        initChart();
        String resultStr = "";
        for (String strRes : results) {
            resultStr += strRes + ";";
        }
        Log.e("Results", resultStr);
        setBusyUI(false, event.getTrialName());
    }

    /**
     * Logs msec between start time and now
     *
     * @param startTime relative to start time in msec; use -1 to set elapsed time to zero
     * @param endTime
     * @param framework framework logging event
     * @param name      string to log for event
     */
    public void logTime(long startTime, long endTime, String framework, String name) {
        long elapsedMsec = (startTime == -1) ? 0 : endTime - startTime;
        Log.e(MainActivity.class.getSimpleName(), framework + ". " + name + " took: " + elapsedMsec);
        resultsStringBuilder.append(framework).append(' ').append(name).append(" took: ").append(elapsedMsec).append(" msec\n");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultsTextView.setText(resultsStringBuilder.toString());
            }
        });
        results.add(String.valueOf(elapsedMsec));
        // update chart data
        addChartData(framework, name, elapsedMsec);
    }

    private void setBusyUI(boolean enabled, String testName) {
        runningTestName = testName;
        if (enabled) {
            runningTests = true;
            resultsStringBuilder.setLength(0);
            resultsTextView.setVisibility(View.VISIBLE);
            chartView.setVisibility(View.GONE);
            enableButtons(false);
            progressBar.setVisibility(View.VISIBLE);
            progressCardView.setVisibility(View.VISIBLE);
        } else {
            runningTests = false;
            resultsTextView.setVisibility(View.GONE);
            if (runningTestName != null) {
                chartView.setVisibility(View.VISIBLE);
            }
            enableButtons(true);
            progressBar.setVisibility(View.GONE);
            progressCardView.setVisibility(View.GONE);
        }
        if (runningTestName != null) {
            resultsLabel.setText(getResources().getString(R.string.results, testName));
            resultsLabel.setVisibility(View.VISIBLE);
        }
    }

    private void initChart() {
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        // note that we show save first because that's how we initialize the DB
        for (String frameworkName : chartEntrySets.keySet()) {
            ArrayList<BarEntry> entrySet = chartEntrySets.get(frameworkName);
            BarDataSet dataSet = new BarDataSet(entrySet, frameworkName);
            dataSet.setColor(getFrameworkColor(frameworkName));
            dataSets.add(dataSet);
        }
        // load data and animate it
        ArrayList<String> xAxisLabels = new ArrayList<>();
        xAxisLabels.add("Save");
        xAxisLabels.add("Load");
        BarData data = new BarData(xAxisLabels, dataSets);
        chartView.setData(data);
        chartView.setDescription(null); // this takes up too much space, so clear it
        chartView.animateXY(200, 200);
        chartView.invalidate();
    }

    private void reset(int resId) {
        setBusyUI(true, getResources().getString(resId));

        results = new ArrayList<>();

        chartEntrySets.clear();
        // the order you add these in is the order they're displayed in
        chartEntrySets.put(DBFlowTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(GreenDaoTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(OrmLiteTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(RealmTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(SqlTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
    }

    private int getFrameworkColor(String framework) {
        // using the 300 line colors from https://material.google.com/style/color.html#color-color-palette
        switch (framework) {
            case DBFlowTester.FRAMEWORK_NAME:
                return Color.rgb(0xF4, 0x43, 0x36); // red #F44336
            case GreenDaoTester.FRAMEWORK_NAME:
                return Color.rgb(0x4C, 0xAF, 0x50); // green #4CAF50
            case OrmLiteTester.FRAMEWORK_NAME:
                return Color.rgb(0xFF, 0x57, 0x22); // deep orange #FF5722
            case RealmTester.FRAMEWORK_NAME:
                return Color.rgb(0x21, 0x96, 0XF3); // blue #2196F3
            case SqlTester.FRAMEWORK_NAME:
                return Color.rgb(0x9C, 0x27, 0xB0); // purple #9C27B0
            default:
                return Color.WHITE;
        }
    }

    private void addChartData(String framework, String category, long value) {
        BarEntry entry = new BarEntry(value, category.equals(SAVE_TIME) ? 0 : 1);
        chartEntrySets.get(framework).add(entry);
    }

    private void enableButtons(boolean enabled) {
        simpleTrialButton.setEnabled(enabled);
        complexTrialButton.setEnabled(enabled);
        simpleTrialButtonRead.setEnabled(enabled);
        complexTrialButtonRead.setEnabled(enabled);
    }

    /**
     * runs simple benchmarks (onClick from R.id.simple)
     *
     * @param v button view
     */

    public void runSimpleTrial(View v) {
        reset(R.string.simple);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runningTests = true;
                Context applicationContext = MainActivity.this.getApplicationContext();
                RealmTester.testAddressItems();
                OrmLiteTester.testAddressItems(applicationContext);
                GreenDaoTester.testAddressItems(applicationContext);
                DBFlowTester.testAddressItems();
                SqlTester.testAddressItems(applicationContext);
                EventBus.getDefault().post(new TrialCompletedEvent(getResources().getString(R.string.simple)));
            }
        }).start();
        System.gc();
    }

    /**
     * runs complex benchmarks (onClick from R.id.complex)
     *
     * @param v button view
     */
    public void runComplexTrial(View v) {
        reset(R.string.complex);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runningTests = true;
                Context applicationContext = MainActivity.this.getApplicationContext();
                RealmTester.testAddressBooks();
                OrmLiteTester.testAddressBooks(applicationContext);
                GreenDaoTester.testAddressBooks(applicationContext);
                DBFlowTester.testAddressBooks();
                SqlTester.testAddressBooks(applicationContext);
                EventBus.getDefault().post(new TrialCompletedEvent(getResources().getString(R.string.complex)));
            }
        }).start();
        System.gc();
    }

    public void runSimpleTrialRead(View view) {
        reset(R.string.simple);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runningTests = true;
                Context applicationContext = MainActivity.this.getApplicationContext();
                RealmTester.testAddressItemsRead();
                OrmLiteTester.testAddressItemsRead(applicationContext);
                GreenDaoTester.testAddressItemsRead(applicationContext);
                DBFlowTester.testAddressItemsRead();
                SqlTester.testAddressItemsRead(applicationContext);
                EventBus.getDefault().post(new TrialCompletedEvent(getResources().getString(R.string.simple)));
            }
        }).start();
        System.gc();
    }

    public void runComplexTrialRead(View view) {
        reset(R.string.complex);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runningTests = true;
                Context applicationContext = MainActivity.this.getApplicationContext();
                RealmTester.testAddressBooksRead();
                OrmLiteTester.testAddressBooksRead(applicationContext);
                GreenDaoTester.testAddressBooksRead(applicationContext);
                DBFlowTester.testAddressBooksRead();
                SqlTester.testAddressBooksRead(applicationContext);
                EventBus.getDefault().post(new TrialCompletedEvent(getResources().getString(R.string.complex)));
            }
        }).start();
        System.gc();
    }

}
