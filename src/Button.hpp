//
// Button.hpp
//
#pragma once

#include <M5StickC.h>

class Button {
public:
	Button(uint8_t _pin) : pin(_pin), isPress(false) {}

	void begin() {
		pinMode(pin, INPUT);
		isPress = read();
	}
	bool read() {
		return digitalRead(pin) == LOW;
	}
	bool isPressed() {
		isPress = read();
		return isPress;
	}
	bool wasPressed() {
		bool isPressPrev = isPress;
		isPressed();
		return (!isPressPrev && isPress);
	}
	bool wasReleased() {
		bool isPressPrev = isPress;
		isPressed();
		return (isPressPrev && !isPress);
	}

private:
	uint8_t pin;
	bool isPress;
};