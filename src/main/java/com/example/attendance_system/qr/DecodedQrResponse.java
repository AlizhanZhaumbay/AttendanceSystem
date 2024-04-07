package com.example.attendance_system.qr;

public class DecodedQrResponse {
    private String qrString;

    public DecodedQrResponse() {

    }

    public DecodedQrResponse(String qrString) {
        this.qrString = qrString;
    }

    public String getQrString() {
        return qrString;
    }

    public void setQrString(String qrString) {
        this.qrString = qrString;
    }
}
