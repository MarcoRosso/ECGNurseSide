package com.marco.constant;

import cn.bmob.v3.BmobObject;

public class PatientIn extends BmobObject{
    private String PatientPassword;
    private String PatientName;
	public String getPatientPassword() {
		return PatientPassword;
	}
	public void setPatientPassword(String patientPassword) {
		PatientPassword = patientPassword;
	}
	public String getPatientName() {
		return PatientName;
	}
	public void setPatientName(String patientName) {
		PatientName = patientName;
	}

}
