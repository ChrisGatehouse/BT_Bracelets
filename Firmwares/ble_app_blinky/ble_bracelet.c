/**
 * Copyright (c) 2013 - 2019, Nordic Semiconductor ASA
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
#include "sdk_common.h"
#include "ble_bracelet.h"
#include "ble_srv_common.h"


/**@brief Function for handling the Write event.
 *
 * @param[in] p_bracelet Service structure.
 * @param[in] p_ble_evt  Event received from the BLE stack.
 */
static void on_write(ble_bracelet_t * p_bracelet, ble_evt_t const * p_ble_evt)
{
    ble_gatts_evt_write_t const * p_evt_write = &p_ble_evt->evt.gatts_evt.params.write;

    if (   (p_evt_write->handle == p_bracelet->led_char_handles.value_handle)
        && (p_evt_write->len == 1)
        && (p_bracelet->led_write_handler != NULL))
    {
        p_bracelet->led_write_handler(p_ble_evt->evt.gap_evt.conn_handle, p_bracelet, p_evt_write->data[0]);
    }
}


void ble_bracelet_on_ble_evt(ble_evt_t const * p_ble_evt, void * p_context)
{
    ble_bracelet_t * p_bracelet = (ble_bracelet_t *)p_context;

    switch (p_ble_evt->header.evt_id)
    {
        case BLE_GATTS_EVT_WRITE:
            on_write(p_bracelet, p_ble_evt);
            break;

        default:
            // No implementation needed.
            break;
    }
}


uint32_t ble_bracelet_init(ble_bracelet_t * p_bracelet, const ble_bracelet_init_t * p_bracelet_init)
{
    uint32_t              err_code;
    ble_uuid_t            ble_uuid;
    ble_add_char_params_t add_char_params;

    // Initialize service structure.
    p_bracelet->led_write_handler = p_bracelet_init->led_write_handler;

    // Add service.
    ble_uuid128_t base_uuid = {BRACELET_UUID_BASE};
    err_code = sd_ble_uuid_vs_add(&base_uuid, &p_bracelet->uuid_type);
    VERIFY_SUCCESS(err_code);

    ble_uuid.type = p_bracelet->uuid_type;
    ble_uuid.uuid = BRACELET_UUID_SERVICE;

    err_code = sd_ble_gatts_service_add(BLE_GATTS_SRVC_TYPE_PRIMARY, &ble_uuid, &p_bracelet->service_handle);
    VERIFY_SUCCESS(err_code);

    // Add Button characteristic.
    memset(&add_char_params, 0, sizeof(add_char_params));
    add_char_params.uuid              = BRACELET_UUID_BUTTON_CHAR;
    add_char_params.uuid_type         = p_bracelet->uuid_type;
    add_char_params.init_len          = sizeof(uint8_t);
    add_char_params.max_len           = sizeof(uint8_t);
    add_char_params.char_props.read   = 1;
    add_char_params.char_props.notify = 1;

    add_char_params.read_access       = SEC_OPEN;
    add_char_params.cccd_write_access = SEC_OPEN;

    err_code = characteristic_add(p_bracelet->service_handle,
                                  &add_char_params,
                                  &p_bracelet->button_char_handles);
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }

    // Add Color characteristic.
    memset(&add_char_params, 0, sizeof(add_char_params));
    add_char_params.uuid             = BRACELET_UUID_COLOR_CHAR;
    add_char_params.uuid_type        = p_bracelet->uuid_type;
    add_char_params.init_len         = 3 * sizeof(uint8_t);
    add_char_params.max_len          = 3 * sizeof(uint8_t);
    add_char_params.char_props.read  = 1;
    add_char_params.char_props.write = 1;

    add_char_params.read_access  = SEC_OPEN;
    add_char_params.write_access = SEC_OPEN;

    err_code = characteristic_add(p_bracelet->service_handle,
                                  &add_char_params,
                                  &p_bracelet->color_char_handles);
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }

    // Add Vibrate characteristic.
    memset(&add_char_params, 0, sizeof(add_char_params));
    add_char_params.uuid             = BRACELET_UUID_VIBRATE_CHAR;
    add_char_params.uuid_type        = p_bracelet->uuid_type;
    add_char_params.init_len         = sizeof(uint8_t);
    add_char_params.max_len          = sizeof(uint8_t);
    add_char_params.char_props.read  = 1;
    add_char_params.char_props.write = 1;

    add_char_params.read_access  = SEC_OPEN;
    add_char_params.write_access = SEC_OPEN;

    err_code = characteristic_add(p_bracelet->service_handle,
                                  &add_char_params,
                                  &p_bracelet->vibrate_char_handles);
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }

    // Add Timer characteristic.
    memset(&add_char_params, 0, sizeof(add_char_params));
    add_char_params.uuid             = BRACELET_UUID_TIMER_CHAR;
    add_char_params.uuid_type        = p_bracelet->uuid_type;
    add_char_params.init_len         = sizeof(uint32_t);
    add_char_params.max_len          = sizeof(uint32_t);
    add_char_params.char_props.read  = 1;
    add_char_params.char_props.write = 1;

    add_char_params.read_access  = SEC_OPEN;
    add_char_params.write_access = SEC_OPEN;

    err_code = characteristic_add(p_bracelet->service_handle,
                                  &add_char_params,
                                  &p_bracelet->timer_char_handles);
    if (err_code != NRF_SUCCESS)
    {
        return err_code;
    }

    // Add LED characteristic.
    memset(&add_char_params, 0, sizeof(add_char_params));
    add_char_params.uuid             = BRACELET_UUID_LED_CHAR;
    add_char_params.uuid_type        = p_bracelet->uuid_type;
    add_char_params.init_len         = sizeof(uint8_t);
    add_char_params.max_len          = sizeof(uint8_t);
    add_char_params.char_props.read  = 1;
    add_char_params.char_props.write = 1;

    add_char_params.read_access  = SEC_OPEN;
    add_char_params.write_access = SEC_OPEN;

    return characteristic_add(p_bracelet->service_handle, &add_char_params, &p_bracelet->led_char_handles);
}


uint32_t ble_bracelet_on_button_change(uint16_t conn_handle, ble_bracelet_t * p_bracelet, uint8_t button_state)
{
    ble_gatts_hvx_params_t params;
    uint16_t len = sizeof(button_state);

    memset(&params, 0, sizeof(params));
    params.type   = BLE_GATT_HVX_NOTIFICATION;
    params.handle = p_bracelet->button_char_handles.value_handle;
    params.p_data = &button_state;
    params.p_len  = &len;

    return sd_ble_gatts_hvx(conn_handle, &params);
}
