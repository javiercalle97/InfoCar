################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../state_machine/fsm.c \
../state_machine/state_machine.c 

OBJS += \
./state_machine/fsm.o \
./state_machine/state_machine.o 

C_DEPS += \
./state_machine/fsm.d \
./state_machine/state_machine.d 


# Each subdirectory must supply rules for building sources it contributes
state_machine/%.o: ../state_machine/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cross GCC Compiler'
	arm-linux-gnueabihf-gcc -I/home/vicente/development/rpi/include_man -I/home/vicente/development/rpi/include_bluetooth -I/home/vicente/development/rpi/include_bluetooth/dbus-1.0 -I/home/vicente/development/rpi/include_bluetooth/gio-unix-2.0 -I/home/vicente/development/rpi/include_bluetooth/glib-2.0 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


