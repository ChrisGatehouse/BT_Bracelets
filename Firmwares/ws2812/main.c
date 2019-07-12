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
#include "nrf_spim.h"

#define SPI_PIN NRF_GPIO_PIN_MAP(1,10)
#define LED_PIN NRF_GPIO_PIN_MAP(1,11)

#define NLEDS 4

#define DECLARE_TX_BUFFER(name, nleds) static uint32_t name[(nleds)*3+2]
#define DECLARE_COLOR_BUFFER(name, nleds) static uint8_t name[(nleds)*3]

DECLARE_TX_BUFFER(tx_buffer, NLEDS);
DECLARE_COLOR_BUFFER(colors, NLEDS);

struct ws2812 {
    uint8_t *colors;
    uint32_t *tx_buffer;
    size_t count;
    NRF_SPIM_Type *spim;
};

uint32_t
ws2812_spi_word(uint8_t byte)
{
    // look up table to convert four bits at a time
    
    static const uint16_t nibbles[] = {
        0x8888, 0x888C, 0x88C8, 0x88CC, 
        0x8C88, 0x8C8C, 0x8CC8, 0x8CCC,
        0xC888, 0xC88C, 0xC8C8, 0xC8CC, 
        0xCC88, 0xCC8C, 0xCCC8, 0xCCCC
    };
    /*static const uint16_t nibbles[] = {
        0x8888, 0x888E, 0x88E8, 0x88EE, 
        0x8E88, 0x8E8E, 0x8EE8, 0x8EEE,
        0xE888, 0xE88E, 0xE8E8, 0xE8EE, 
        0xEE88, 0xEE8E, 0xEEE8, 0xEEEE
    };*/

    return (nibbles[byte & 0x0F] << 16) | (nibbles[(byte >> 4) & 0xF]);
}

void
ws2812_spi_expand_buffer(uint8_t *in, uint32_t *out, size_t length)
{
    int i;
    for (i = 0; i < length; i++) {
        out[i+1] = ws2812_spi_word(in[i]);
    }
    out[i+1] = 0x0;
    out[0] = 0x0;
}

void
ws2812_spi_show(struct ws2812 *leds)
{
    ws2812_spi_expand_buffer(leds->colors, leds->tx_buffer, leds->count * 3);
    nrf_spim_task_trigger(leds->spim, NRF_SPIM_TASK_START);
}

void
ws2812_set_rgb(struct ws2812 *leds, int i, uint8_t r, uint8_t g, uint8_t b)
{
    leds->colors[i*3] = g;
    leds->colors[i*3+1] = r;
    leds->colors[i*3+2] = b;
}

void
ws2812_set_int(struct ws2812 *leds, int i, uint32_t color)
{
    leds->colors[i*3] = (uint8_t)(color >> 8); // green
    leds->colors[i*3+1] = (uint8_t)(color >> 16); // red
    leds->colors[i*3+2] = (uint8_t)color; // blue
}

void
ws2812_init(struct ws2812 *leds, 
            int count, int pin, int aux, NRF_SPIM_Type *spim,
            uint8_t *colors, uint32_t *tx_buffer)
{
    leds->count = count;
    leds->spim = spim;
    leds->colors = colors;
    leds->tx_buffer = tx_buffer;

    nrf_gpio_cfg_output(pin);
    nrf_gpio_pin_clear(pin);
    nrf_spim_configure(spim, NRF_SPIM_MODE_0, NRF_SPIM_BIT_ORDER_MSB_FIRST);
    nrf_spim_pins_set(spim,
                      aux,
                      pin,
                      NRF_SPIM_PIN_NOT_CONNECTED);
    nrf_spim_tx_buffer_set(spim, (void *)tx_buffer, (count*3+2)*4);
    nrf_spim_rx_buffer_set(spim, NULL, 0);
    nrf_spim_frequency_set(spim, 0x2A000000);
    nrf_spim_enable(spim);
}

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
