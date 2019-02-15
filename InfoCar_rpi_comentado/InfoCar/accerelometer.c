#include <wiringPiI2C.h>


#include "accerelometer.h"

int fd;

void setupIMU() {
	fd = wiringPiI2CSetup(IMU_AD);

//	wiringPiI2CWriteReg8(fd, REG5_AD, REG5_CONF);
//	wiringPiI2CWriteReg8(fd, REG6_AD, REG6_CONF);

	wiringPiI2CWriteReg8(fd, REG1_AD, REG1_CONF);
	wiringPiI2CWriteReg8(fd, REG2_AD, REG2_CONF);
	wiringPiI2CWriteReg8(fd, REG3_AD, REG3_CONF);
}


// Accelerometer

int32_t get_x_acceleration() {
	uint8_t xLow = wiringPiI2CReadReg8 (fd, XLOW_AD);
	uint8_t xHigh = wiringPiI2CReadReg8 (fd, XHIGH_AD);

	int16_t x_acc = ((int16_t)xHigh << 8) | xLow;

	return x_acc;
}

int32_t get_y_acceleration() {
	uint8_t yLow = wiringPiI2CReadReg8 (fd, YLOW_AD);
	uint8_t yHigh = wiringPiI2CReadReg8 (fd, YHIGH_AD);

	int16_t y_acc = ((int16_t)yHigh << 8) | yLow;

	return y_acc;
}

int32_t get_z_acceleration() {
	uint8_t zLow = wiringPiI2CReadReg8 (fd, ZLOW_AD);
	uint8_t zHigh = wiringPiI2CReadReg8 (fd, ZHIGH_AD);

	int16_t z_acc = ((int16_t)zHigh << 8) | zLow;

	return z_acc;
}


// Gyroscope

int32_t get_x_gyro(){
	uint8_t x_gyro_Low = wiringPiI2CReadReg8 (fd, ANG_X_LOW_AD);
	uint8_t x_gyro_High = wiringPiI2CReadReg8 (fd, ANG_X_HIGH_AD);

	int16_t x_gyro = ((int16_t)x_gyro_High << 8) | x_gyro_Low;

	return x_gyro;
}

int32_t get_y_gyro(){
	uint8_t y_gyro_Low = wiringPiI2CReadReg8 (fd, ANG_Y_LOW_AD);
	uint8_t y_gyro_High = wiringPiI2CReadReg8 (fd, ANG_Y_HIGH_AD);

	int16_t y_gyro = ((int16_t)y_gyro_High << 8) | y_gyro_Low;

	return y_gyro;
}

int32_t get_z_gyro(){
	uint8_t z_gyro_Low = wiringPiI2CReadReg8 (fd, ANG_Z_LOW_AD);
	uint8_t z_gyro_High = wiringPiI2CReadReg8 (fd, ANG_Z_HIGH_AD);

	int16_t z_gyro = ((int16_t)z_gyro_High << 8) | z_gyro_Low;

	return z_gyro;
}




