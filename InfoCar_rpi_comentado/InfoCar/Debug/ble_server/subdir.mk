################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../ble_server/advertising.c \
../ble_server/gatt-service.c 

OBJS += \
./ble_server/advertising.o \
./ble_server/gatt-service.o 

C_DEPS += \
./ble_server/advertising.d \
./ble_server/gatt-service.d 


# Each subdirectory must supply rules for building sources it contributes
ble_server/%.o: ../ble_server/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross GCC Compiler'
	arm-linux-gnueabihf-gcc -I/home/vicente/development/rpi/include_man -I/home/vicente/development/rpi/include_bluetooth -I/home/vicente/development/rpi/include_bluetooth/dbus-1.0 -I/home/vicente/development/rpi/include_bluetooth/gio-unix-2.0 -I/home/vicente/development/rpi/include_bluetooth/glib-2.0 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


