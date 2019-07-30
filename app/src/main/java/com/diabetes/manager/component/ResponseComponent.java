package com.diabetes.manager.component;

import java.util.List;

public class ResponseComponent {

    //Variabel pada adater harus sesuai dengan JSON PHP
    String error, status;
    List<RecordsComponent> records;

    public String getError() {

        return error;
    }

    public String getStatus() {

        return status;
    }

    public List<RecordsComponent> getRecords() {

        return records;
    }
}
