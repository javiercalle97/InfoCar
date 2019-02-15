################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../GPS.c \
../accerelometer.c \
../bt_iface.c \
../main.c \
../thermometer.c 

OBJS += \
./GPS.o \
./accerelometer.o \
./bt_iface.o \
./main.o \
./thermometer.o 

C_DEPS += \
./GPS.d \
./accerelometer.d \
./bt_iface.d \
./main.d \
./thermometer.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross GCC Compiler'
	arm-linux-gnueabihf-gcc -I/home/vicente/development/rpi/include_man -I/home/vicente/development/rpi/include_bluetooth -I/home/vicente/development/rpi/include_bluetooth/dbus-1.0 -I/home/vicente/development/rpi/include_bluetooth/gio-unix-2.0 -I/home/vicente/development/rpi/include_bluetooth/glib-2.0 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


