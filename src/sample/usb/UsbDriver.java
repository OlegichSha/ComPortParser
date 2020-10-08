// https://micro-pi.ru/jssc-работаем-com-портом-java-raspberry/

package sample.usb;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPort.*;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import sample.Controller;

import java.util.List;

public class UsbDriver {
    private static UsbDriver usbDriver;
    private static SerialPort serialPort;

    private UsbDriver() {
    }

    // Singleton
    public static UsbDriver GetInstance() {
        if (usbDriver == null) {
            usbDriver = new UsbDriver();
        }
        return usbDriver;
    }


    // Открытие COM порта
    public void OpenPort(String portDescriptor, String baudRate) {
        int br = baudRate != null ? Integer.parseInt( baudRate) : 0;
        serialPort = SerialPort.getCommPort(portDescriptor);
        try {
            serialPort.openPort();
//            PacketListener packetListener = new PacketListener();
//            AddListener(packetListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Добавить слушателя шины
    public void AddListener(SerialPortDataListener listener)
    {
        serialPort.addDataListener(listener);
    }


    // Состояние com порта
    public boolean IsOpen() {
        boolean result = false;
        if (serialPort != null) {
            result = serialPort.isOpen();
        }
        return result;
    }

    // Закрытие COM порта
    public void ClosePort() {
        if (serialPort.isOpen()) {
            try {
                serialPort.closePort();
                serialPort.removeDataListener();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Получение списка портов
    public SerialPort[] GetUsbPort() {
        return SerialPort.getCommPorts();
    }


    private final class PacketListener implements SerialPortDataListener
    {
        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }


        @Override
        public void serialEvent(SerialPortEvent event)
        {
            byte[] newData = event.getReceivedData();
            System.out.println("Received data of size: " + newData.length);
            for (int i = 0; i < newData.length; ++i)
                System.out.print((char)newData[i]);
            System.out.println("\n");
        }
    }

}

