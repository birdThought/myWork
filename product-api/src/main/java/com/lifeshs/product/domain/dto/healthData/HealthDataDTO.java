package com.lifeshs.product.domain.dto.healthData;

import com.lifeshs.product.domain.po.device.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


public class HealthDataDTO {
	
	private String healthType;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date measureDate;

	private TMeasureBloodpressure bloodpressure;
	
	private TMeasureLunginstrument lunginstrument;
	
	private TMeasureGlucometer glucometer;
	
	private TMeasureOxygen oxygen;
	
	private TMeasureBodyfatscale bodyfatscale;
	
	private TMeasureHeartrate heartrate;
	
	private TMeasureTemperature temperature;

	public String getHealthType() {
		return healthType;
	}

	public void setHealthType(String healthType) {
		this.healthType = healthType;
	}
	
	public Date getMeasureDate() {
		return measureDate;
	}

	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}

	public TMeasureBloodpressure getBloodpressure() {
		return bloodpressure;
	}

	public void setBloodpressure(TMeasureBloodpressure bloodpressure) {
		this.bloodpressure = bloodpressure;
	}

	public TMeasureLunginstrument getLunginstrument() {
		return lunginstrument;
	}

	public void setLunginstrument(TMeasureLunginstrument lunginstrument) {
		this.lunginstrument = lunginstrument;
	}

	public TMeasureGlucometer getGlucometer() {
		return glucometer;
	}

	public void setGlucometer(TMeasureGlucometer glucometer) {
		this.glucometer = glucometer;
	}

	public TMeasureOxygen getOxygen() {
		return oxygen;
	}

	public void setOxygen(TMeasureOxygen oxygen) {
		this.oxygen = oxygen;
	}

	public TMeasureBodyfatscale getBodyfatscale() {
		return bodyfatscale;
	}

	public void setBodyfatscale(TMeasureBodyfatscale bodyfatscale) {
		this.bodyfatscale = bodyfatscale;
	}

	public TMeasureHeartrate getHeartrate() {
		return heartrate;
	}

	public void setHeartrate(TMeasureHeartrate heartrate) {
		this.heartrate = heartrate;
	}

	public TMeasureTemperature getTemperature() {
		return temperature;
	}

	public void setTemperature(TMeasureTemperature temperature) {
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		return "HealthDataDTO [healthType=" + healthType + ", measureDate="
				+ measureDate + ", bloodpressure=" + bloodpressure
				+ ", lunginstrument=" + lunginstrument + ", glucometer="
				+ glucometer + ", oxygen=" + oxygen + ", bodyfatscale="
				+ bodyfatscale + ", heartrate=" + heartrate + ", temperature="
				+ temperature + "]";
	}
}
