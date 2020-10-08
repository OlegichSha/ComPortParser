package sample;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.usb.UsbDriver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea inputTextField;

    @FXML
    private TextField sendTextField;

    @FXML
    private Button buttonSend;

    @FXML
    private ComboBox<String> listComPort;

    @FXML
    private Button connectButton;

    @FXML
    private ComboBox<String> portSpeed;

    @FXML
    private Button updateButton;

    UsbDriver usb = UsbDriver.GetInstance();

    @FXML
    public void initialize() {

        SerialPort[] ports = SerialPort.getCommPorts();
        ObservableList<String> listComPorts = FXCollections.observableArrayList();
        for(SerialPort port : ports)
        {
            listComPorts.add(port.getSystemPortName());
        }
        listComPort.setItems(listComPorts);

        inputTextField.appendText("Hello");
        inputTextField.appendText(System.lineSeparator());

    }


    // Подключаемся/Отключаемся к Usb
    public void ButtonConnect(){
        if(usb.IsOpen()){
            usb.ClosePort();
            if(!usb.IsOpen()) {
                connectButton.setText("Connect");
            }
        }
        else {
            if(listComPort.getValue() != null)
            {
                usb.OpenPort(listComPort.getValue(), portSpeed.getValue());
                    if(usb.IsOpen()) {
                        Controller.LisenerUsb listener = new Controller.LisenerUsb();
                        usb.AddListener(listener);
                        connectButton.setText("Disconnect");
                    }
            }
        }

    }


    private final class LisenerUsb implements SerialPortDataListener
    {
        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }


        @Override
        public void serialEvent(SerialPortEvent event)
        {
            byte[] newData = event.getReceivedData();
            System.out.println("Received data to Controller of size: " + newData.length);

            // Ожидаем завершения потока UI
            Platform.runLater(() -> {
                        for (int i = 0; i < newData.length; ++i) {
                            String str = Integer.toHexString(newData[i]);

                            inputTextField.appendText(str);
                        }
                    });

            inputTextField.appendText(System.lineSeparator());
        }
    }


}
