################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../gdbus/client.c \
../gdbus/error.c \
../gdbus/mainloop.c \
../gdbus/object.c \
../gdbus/polkit.c \
../gdbus/watch.c 

OBJS += \
./gdbus/client.o \
./gdbus/error.o \
./gdbus/mainloop.o \
./gdbus/object.o \
./gdbus/polkit.o \
./gdbus/watch.o 

C_DEPS += \
./gdbus/client.d \
./gdbus/error.d \
./gdbus/mainloop.d \
./gdbus/object.d \
./gdbus/polkit.d \
./gdbus/watch.d 


# Each subdirectory must supply rules for building sources it contributes
gdbus/%.o: ../gdbus/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross GCC Compiler'
	arm-linux-gnueabihf-gcc -I/home/vicente/development/rpi/include_man -I/home/vicente/development/rpi/include_bluetooth -I/home/vicente/development/rpi/include_bluetooth/dbus-1.0 -I/home/vicente/development/rpi/include_bluetooth/gio-unix-2.0 -I/home/vicente/development/rpi/include_bluetooth/glib-2.0 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


