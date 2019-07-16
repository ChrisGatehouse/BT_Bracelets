/**
 * Copyright (c) 2019 Luke Phillips
 * lcphillips8086@gmail.com
 *
 * All rights reserved.
 */

#include <stdint.h>
#include "nrf.h"
#include "nrf_delay.h"
#include "nrf_gpio.h"

#include "ws2812_spi.h"

#define SPI_PIN NRF_GPIO_PIN_MAP(1,10)
#define LED_PIN NRF_GPIO_PIN_MAP(1,11)

#define NLEDS 4

DECLARE_TX_BUFFER(tx_buffer, NLEDS);
DECLARE_COLOR_BUFFER(colors, NLEDS);

int
main(void)
{
    /* Initialize SPIM. */
    struct ws2812 leds;
    ws2812_init(&leds, NLEDS, SPI_PIN, 100, NRF_SPIM0, colors, tx_buffer);
    ws2812_set_rgb(&leds, 0, 0, 0, 0);
    ws2812_set_rgb(&leds, 1, 0, 0, 0);
    ws2812_set_rgb(&leds, 2, 0, 0, 0);
    ws2812_set_rgb(&leds, 3, 0, 0, 0);
    ws2812_spi_show(&leds);

    /* Toggle LEDs. */
    while (true)
    {

        nrf_delay_ms(1000);
        ws2812_set_rgb(&leds, 0, 255, 64, 200);
        ws2812_spi_show(&leds);

        nrf_delay_ms(1000);
        ws2812_set_rgb(&leds, 1, 0, 200, 200);
        ws2812_spi_show(&leds);

        nrf_delay_ms(1000);
        ws2812_set_rgb(&leds, 2, 32, 32, 16);
        ws2812_spi_show(&leds);

        nrf_delay_ms(1000);
        ws2812_set_rgb(&leds, 3, 0, 200, 0);
        ws2812_spi_show(&leds);
    }
}
