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
#define SPI_BUFFER_SIZE (NLEDS*3 + 1) * 4
#define PATTERN 0x88

static uint8_t colors[NLEDS*3] = {
        255, 0, 0, 
        0, 255, 0,
        0, 0, 255,
        0, 255, 255
};

static uint32_t tx_buffer[NLEDS*3+1];
static uint32_t rx_buffer[NLEDS*3+1];

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
        out[i] = ws2812_spi_word(in[i]);
    }
    out[i] = 0x0;
}

int
main(void)
{
    /*for (int i = 0; i < SPI_BUFFER_SIZE; i++) {
        ((uint8_t *)tx_buffer)[i] = 0x88;
    }
    for (int i = 0; i < 12; i++) {
        ((uint8_t *)tx_buffer)[i] = 0xCC;
    }
    ((uint8_t *)tx_buffer)[SPI_BUFFER_SIZE-1] = 0x00;*/

    ws2812_spi_expand_buffer(colors, tx_buffer, NLEDS*3);

    /* Initialize SPIM. */
    nrf_gpio_cfg_output(LED_PIN);
    nrf_gpio_cfg_output(SPI_PIN);
    nrf_gpio_pin_clear(SPI_PIN);
    nrf_spim_configure(NRF_SPIM0, NRF_SPIM_MODE_0, NRF_SPIM_BIT_ORDER_MSB_FIRST);
    nrf_spim_pins_set(NRF_SPIM0,
                      LED_PIN,
                      SPI_PIN,
                      NRF_SPIM_PIN_NOT_CONNECTED);
    nrf_spim_tx_buffer_set(NRF_SPIM0, (void *)tx_buffer, SPI_BUFFER_SIZE);
    nrf_spim_rx_buffer_set(NRF_SPIM0, (void *)rx_buffer, SPI_BUFFER_SIZE);
    nrf_spim_frequency_set(NRF_SPIM0, 0x2A000000);
    nrf_spim_enable(NRF_SPIM0);
    //nrf_spim_task_trigger(NRF_SPIM0, NRF_SPIM_TASK_START);

    /*for (int i = 0; i < SPI_BUFFER_SIZE; i++) {
        tx_buffer[i] = 0;
    }*/

    /* Toggle LEDs. */
    //uint32_t speed = 0x20000000;
    while (true)
    {
        //if (speed > 0x40000000) speed = 0x20000000;

        nrf_delay_ms(500);
        //nrf_spim_frequency_set(NRF_SPIM0, speed);
        nrf_spim_task_trigger(NRF_SPIM0, NRF_SPIM_TASK_START);
        //speed += 0x00800000;
        //nrf_gpio_pin_toggle(LED_PIN);
    }
}
