package net.ucoz.lexxx3.speedtaxo;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import de.nitri.gauge.Gauge;


public class MainActivity extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";

    int rpm = 0;

    TextView textView;
    ImageView turnLeftLamp,turnRightLamp,HightLamp,LightLamp,FogfLamp,FogrLamp,VarningLamp,OilVarningLamp,CheckLamp,FuelLamp,TempLamp;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;




    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        // Определение метода обратного вызова, который вызывается при приеме данных.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;

            try {

                data = new String(arg0, "UTF-8");

               // tvAppend(textView, data);
                go(data);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
         }
        };


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.

                                serialPort.setBaudRate(115200);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);

                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                }
            }

            ;
        };


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
            textView = (TextView) findViewById(R.id.textView);
            turnRightLamp  = (ImageView) findViewById(R.id.turnRight);
            turnLeftLamp  = (ImageView) findViewById(R.id.turnLeft);
            LightLamp  = (ImageView) findViewById(R.id.Light);
            HightLamp  = (ImageView) findViewById(R.id.Hight);
            FogfLamp  = (ImageView) findViewById(R.id.FogF);
            FogrLamp  = (ImageView) findViewById(R.id.FogR);
            VarningLamp  = (ImageView) findViewById(R.id.Varning);
            CheckLamp  = (ImageView) findViewById(R.id.Check);
            OilVarningLamp  = (ImageView) findViewById(R.id.OilVarning);
            FuelLamp  = (ImageView) findViewById(R.id.FuelLamp);
            TempLamp  = (ImageView) findViewById(R.id.TempLamp);


           // final Gauge gaugeTaxo = findViewById(R.id.gaugeTaxo);


           // gaugeTaxo.moveToValue(rpm);
            //gaugeSpeed.moveToValue(Integer.parseInt(cats[1]));
           //gaugeFuel.moveToValue(35);
             //gaugeTemp.moveToValue(Integer.parseInt(cats[23]));
             //imageViewCharge.setImageResource(mImageArray[index]);



            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(broadcastReceiver, filter);

            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
            if (!usbDevices.isEmpty()) {
                boolean keep = true;
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    device = entry.getValue();
                    int deviceVID = device.getVendorId();
                    if (deviceVID == 0x2341 || deviceVID == 0x0403 || deviceVID == 0x1A86)//Arduino Vendor ID
                    {
                        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        usbManager.requestPermission(device, pi);
                        keep = false;
                    } else {
                        connection = null;
                        device = null;
                    }
                        if (!keep)
                        break;
                }
            }





        }




        private void tvAppend(TextView tv, CharSequence text) {

            final TextView ftv = tv;

            final CharSequence ftext = text;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {



                    ftv.append(ftext);

                }
            });

        }


    private void go(String data) {
        final String datas =  data;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String temp = null;
                String taxo = null;
                String speed = null;
                String fuel = null;


         char[] myTurn = datas.toCharArray();

                if (myTurn[20] == ('1')) {
                    MainActivity.this.turnLeftLamp.setVisibility(View.VISIBLE);
                    MainActivity.this.turnRightLamp.setVisibility(View.INVISIBLE);

                }
                if (myTurn[20] == ('2')) {
                    MainActivity.this.turnLeftLamp.setVisibility(View.INVISIBLE);
                    MainActivity.this.turnRightLamp.setVisibility(View.VISIBLE);
                }
                if (myTurn[20] == ('3')) {
                    MainActivity.this.turnLeftLamp.setVisibility(View.VISIBLE);
                    MainActivity.this.turnRightLamp.setVisibility(View.VISIBLE);
                }
                if (myTurn[20] == ('0')) {
                    MainActivity.this.turnLeftLamp.setVisibility(View.INVISIBLE);
                    MainActivity.this.turnRightLamp.setVisibility(View.INVISIBLE);
                }


                if (myTurn[21] == ('1')) {
                    MainActivity.this.LightLamp.setVisibility(View.VISIBLE);
                    MainActivity.this.HightLamp.setVisibility(View.INVISIBLE);
                }
                if (myTurn[21] == ('2')) {
                    MainActivity.this.LightLamp.setVisibility(View.INVISIBLE);
                    MainActivity.this.HightLamp.setVisibility(View.VISIBLE);

                }
                if (myTurn[21] == ('3')) {
                    MainActivity.this.LightLamp.setVisibility(View.VISIBLE);
                    MainActivity.this.HightLamp.setVisibility(View.VISIBLE);
                }
                if (myTurn[21] == ('0')) {
                    MainActivity.this.LightLamp.setVisibility(View.INVISIBLE);
                    MainActivity.this.HightLamp.setVisibility(View.INVISIBLE);
                }


                if (myTurn[22] == ('1')) {
                    MainActivity.this.FogfLamp.setVisibility(View.VISIBLE);
                    MainActivity.this.FogrLamp.setVisibility(View.INVISIBLE);
                }
                if (myTurn[22] == ('2')) {
                    MainActivity.this.FogfLamp.setVisibility(View.INVISIBLE);
                    MainActivity.this.FogrLamp.setVisibility(View.VISIBLE);
                }
                if (myTurn[22] == ('3')) {
                    MainActivity.this.FogfLamp.setVisibility(View.VISIBLE);
                    MainActivity.this.FogrLamp.setVisibility(View.VISIBLE);
                }
                if (myTurn[22] == ('0')) {
                    MainActivity.this.FogfLamp.setVisibility(View.INVISIBLE);
                    MainActivity.this.FogrLamp.setVisibility(View.INVISIBLE);
                }




                speed=""+myTurn[0]+myTurn[1]+myTurn[2];
                final Gauge gaugeSpeed = findViewById(R.id.gaugeSpeed);
                gaugeSpeed.setValue(Integer.valueOf(speed));


                taxo=""+myTurn[3]+myTurn[4]+myTurn[5]+myTurn[6];
              final Gauge gaugeTaxo = findViewById(R.id.gaugeTaxo);
                 gaugeTaxo.setValue(Integer.valueOf(taxo));



                fuel=""+myTurn[7]+myTurn[8]+myTurn[9]+myTurn[10];
                final Gauge gaugeFuel = findViewById(R.id.gaugeFuel);
                gaugeFuel.setValue(Integer.valueOf(fuel));
                if (Integer.valueOf(fuel) <= 8) {                              // включение лампы топлива
                    MainActivity.this.TempLamp.setVisibility(View.VISIBLE);
                }else  MainActivity.this.TempLamp.setVisibility(View.INVISIBLE);

                temp=""+myTurn[11]+myTurn[12]+myTurn[13]+myTurn[14];
                textView.setText("Температура "+ temp);
                final Gauge gaugeTemp = findViewById(R.id.gaugeTemp);
                gaugeTemp.setValue(Integer.valueOf(temp));
                if (Integer.valueOf(temp) >= 100) {                              //темп включения лампы температуры
                    MainActivity.this.TempLamp.setVisibility(View.VISIBLE);
                 }else  MainActivity.this.TempLamp.setVisibility(View.INVISIBLE);

        textView.setText("Температура внутри "+""+myTurn[23]+myTurn[24]+myTurn[25]+myTurn[26]+myTurn[27]+"Напряжение "+""+myTurn[15]+myTurn[16]+myTurn[17]+myTurn[18]+myTurn[19]);





            }
        });

    }




}
