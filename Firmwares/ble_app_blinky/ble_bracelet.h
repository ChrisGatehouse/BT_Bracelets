/**
 * Copyright (c) 2015 - 2019, Nordic Semiconductor ASA
 * Copyright (c) 2019, Luke Phillips
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form, except as embedded into a Nordic
 *    Semiconductor ASA integrated circuit in a product or a software update for
 *    such product, must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. Neither the name of Nordic Semiconductor ASA nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * 4. This software, with or without modification, must only be used with a
 *    Nordic Semiconductor ASA integrated circuit.
 *
 * 5. Any software provided in binary form under this license must not be reverse
 *    engineered, decompiled, modified and/or disassembled.
 *
 * THIS SOFTWARE IS PROVIDED BY NORDIC SEMICONDUCTOR ASA "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY, NONINFRINGEMENT, AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL NORDIC SEMICONDUCTOR ASA OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

#ifndef BLE_BRACELET_H
#define BLE_BRACELET_H

#include <stdint.h>
#include <stdbool.h>
#include "ble.h"
#include "ble_srv_common.h"
#include "nrf_sdh_ble.h"

#ifdef __cplusplus
extern "C" {
#endif

#define BLE_BRACELET_BLE_OBSERVER_PRIO 2

#define BLE_BRACELET_DEF(_name)                                                                          \
static ble_bracelet_t _name;                                                                             \
NRF_SDH_BLE_OBSERVER(_name ## _obs,                                                                 \
                     BLE_BRACELET_BLE_OBSERVER_PRIO,                                                     \
                     ble_bracelet_on_ble_evt, &_name)

#define BRACELET_UUID_BASE        {0x23, 0xD1, 0xBC, 0xEA, 0x5F, 0x78, 0x23, 0x15, \
                              0xDE, 0xEF, 0x12, 0x12, 0x00, 0x00, 0x00, 0x00}
#define BRACELET_UUID_SERVICE      0x1523
#define BRACELET_UUID_BUTTON_CHAR  0x1524
#define BRACELET_UUID_LED_CHAR     0x1525
#define BRACELET_UUID_COLOR_CHAR   0x1526
#define BRACELET_UUID_VIBRATE_CHAR 0x1527
#define BRACELET_UUID_TIMER_CHAR   0x1528


// Forward declaration of the ble_bracelet_t type.
typedef struct ble_bracelet_s ble_bracelet_t;

typedef void (*ble_bracelet_led_write_handler_t) (uint16_t conn_handle, ble_bracelet_t * p_bracelet, uint8_t new_state);
typedef void (*ble_bracelet_color_write_handler_t) (uint16_t conn_handle, ble_bracelet_t * p_bracelet, uint32_t new_state);
typedef void (*ble_bracelet_vibrate_write_handler_t) (uint16_t conn_handle, ble_bracelet_t * p_bracelet, uint8_t new_state);
typedef void (*ble_bracelet_timer_write_handler_t) (uint16_t conn_handle, ble_bracelet_t * p_bracelet, uint32_t new_state);

/* Service init structure. This structure contains all options and data needed for
 * initialization of the service. */
typedef struct
{
    ble_bracelet_led_write_handler_t led_write_handler; /**< Event handler to be called when the LED Characteristic is written. */
    ble_bracelet_color_write_handler_t color_write_handler; /**< Event handler to be called when the Color Characteristic is written. */
    ble_bracelet_vibrate_write_handler_t vibrate_write_handler; /**< Event handler to be called when the Vibrate Characteristic is written. */
    ble_bracelet_timer_write_handler_t timer_write_handler; /**< Event handler to be called when the Timer Characteristic is written. */
} ble_bracelet_init_t;

/* Service structure. This structure contains various status information for the service. */
struct ble_bracelet_s
{
    uint16_t                    service_handle;      /**< Handle of service (as provided by the BLE stack). */
    ble_gatts_char_handles_t    led_char_handles;    /**< Handles related to the LED Characteristic. */
    ble_gatts_char_handles_t    color_char_handles;    /**< Handles related to the Color Characteristic. */
    ble_gatts_char_handles_t    vibrate_char_handles;    /**< Handles related to the Vibrate Characteristic. */
    ble_gatts_char_handles_t    timer_char_handles;    /**< Handles related to the Timer Characteristic. */
    ble_gatts_char_handles_t    button_char_handles; /**< Handles related to the Button Characteristic. */
    uint8_t                     uuid_type;           /**< UUID type for the service. */
    ble_bracelet_led_write_handler_t led_write_handler; /**< Event handler to be called when the LED Characteristic is written. */
    ble_bracelet_color_write_handler_t color_write_handler; /**< Event handler to be called when the Color Characteristic is written. */
    ble_bracelet_vibrate_write_handler_t vibrate_write_handler; /**< Event handler to be called when the Vibrate Characteristic is written. */
    ble_bracelet_timer_write_handler_t timer_write_handler; /**< Event handler to be called when the Timer Characteristic is written. */
};


/**@brief Function for initializing the srvice.
 *
 * @param[out] p_bracelet    Service structure. This structure must be supplied by
 *                        the application. It is initialized by this function and will later
 *                        be used to identify this particular service instance.
 * @param[in] p_bracelet_init  Information needed to initialize the service.
 *
 * @retval NRF_SUCCESS If the service was initialized successfully. Otherwise, an error code is returned.
 */
uint32_t ble_bracelet_init(ble_bracelet_t * p_bracelet, const ble_bracelet_init_t * p_bracelet_init);


/**@brief Function for handling the application's BLE stack events.
 *
 * @details This function handles all events from the BLE stack that are of interest to the service.
 *
 * @param[in] p_ble_evt  Event received from the BLE stack.
 * @param[in] p_context  Service structure.
 */
void ble_bracelet_on_ble_evt(ble_evt_t const * p_ble_evt, void * p_context);


/**@brief Function for sending a button state notification.
 *
 ' @param[in] conn_handle   Handle of the peripheral connection to which the button state notification will be sent.
 * @param[in] p_bracelet    Service structure.
 * @param[in] button_state  New button state.
 *
 * @retval NRF_SUCCESS If the notification was sent successfully. Otherwise, an error code is returned.
 */
uint32_t ble_bracelet_on_button_change(uint16_t conn_handle, ble_bracelet_t * p_bracelet, uint8_t button_state);


#ifdef __cplusplus
}
#endif

#endif // BLE_BRACELET_H

/** @} */
