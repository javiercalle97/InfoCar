//desde la carpeta proyecto
//para compilar: gcc GPS.c -o GPS -lwiringPi
// para ejecutar: ./GPS

#include <wiringPi.h>
#include <wiringSerial.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
//#include "GPS.h"


static const char *dispositivo = "/dev/serial0";	//nombre o direccion del gps
static const int baudios = 9600;					//velocidad de toma de datos

int fd;										//file descriptor
int num_datos;
int recibido;
int i, j;
char ch[10000];
int comas;
char velocidad[5]; //300km/h son 161 nudos, nunca vamos a superar 5, 3 para numeros enteros y 2 para decimales
int aux;		//se utiliza para poner en orden los datos en hora y velocidad
char horaFinal[6]; //hora convertida, ser� transmitida
int32_t velocidad_int32;
int32_t hora_int32;
int cambiaHora;


int32_t get_GPS_speed() { //metodo principal

	int32_t velocidad = 0;

	fd = serialOpen(dispositivo, baudios); //inicializamos el dispositivo
	if (fd != -1) {						//si -1 entonces no se ha inicializado

		num_datos = serialDataAvail(fd); //nos dice el numero de datos disponible
		if (num_datos == -1) {
			//(void) printf("error datos\n");
			return velocidad;
		}
		velocidad = obtenerDatos(fd);
	}
	serialClose(fd);

	return velocidad;

}

int32_t obtenerDatos(int fd_aux) {

	for (i = 0; i <= 10000; i++) {
		ch[i] = serialGetchar(fd_aux);

		if (ch[i] == 'V' && ch[i - 1] == 'P') {	//comprobamos que ha llegado al menos hasta $GPVTG para asi tener toda la info de $GPRMC

			for (i = 0; i <= 10000; i++) {//volvemos a recorrer el tramo que acabamos de recortar

				if (ch[i] == 'R' && ch[i - 1] == 'P') {	//es el unico en el que hay R
					comas = 0;
					for (j = i; j <= 10000; j++) {
						if(comas == 7) {	//velocidad
							aux = 0;
							for (; j <= 10000; j++) {
								if (ch[j] != ',') {
									velocidad[aux] = ch[j];
									aux++;
								} else
									return (int32_t) (atof(velocidad) * 100);
							}
						} else {
							if (ch[j] == ',')
								comas++;
						}
					}
				}
			}
		}
	}
	return 0;
}





