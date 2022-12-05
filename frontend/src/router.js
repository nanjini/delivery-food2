
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import OrderManager from "./components/listers/OrderCards"
import OrderDetail from "./components/listers/OrderDetail"

import MenuView from "./components/MenuView"
import MenuViewDetail from "./components/MenuViewDetail"
import OrderStateView from "./components/OrderStateView"
import OrderStateViewDetail from "./components/OrderStateViewDetail"
import OrderManagementManager from "./components/listers/OrderManagementCards"
import OrderManagementDetail from "./components/listers/OrderManagementDetail"

import OrderDetailView from "./components/OrderDetailView"
import OrderDetailViewDetail from "./components/OrderDetailViewDetail"

import PaymentManager from "./components/listers/PaymentCards"
import PaymentDetail from "./components/listers/PaymentDetail"

import DeliveryManager from "./components/listers/DeliveryCards"
import DeliveryDetail from "./components/listers/DeliveryDetail"

import DeliveryStateView from "./components/DeliveryStateView"
import DeliveryStateViewDetail from "./components/DeliveryStateViewDetail"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/orders',
                name: 'OrderManager',
                component: OrderManager
            },
            {
                path: '/orders/:id',
                name: 'OrderDetail',
                component: OrderDetail
            },

            {
                path: '/menus',
                name: 'MenuView',
                component: MenuView
            },
            {
                path: '/menus/:id',
                name: 'MenuViewDetail',
                component: MenuViewDetail
            },
            {
                path: '/orderStates',
                name: 'OrderStateView',
                component: OrderStateView
            },
            {
                path: '/orderStates/:id',
                name: 'OrderStateViewDetail',
                component: OrderStateViewDetail
            },
            {
                path: '/orderManagements',
                name: 'OrderManagementManager',
                component: OrderManagementManager
            },
            {
                path: '/orderManagements/:id',
                name: 'OrderManagementDetail',
                component: OrderManagementDetail
            },

            {
                path: '/orderDetails',
                name: 'OrderDetailView',
                component: OrderDetailView
            },
            {
                path: '/orderDetails/:id',
                name: 'OrderDetailViewDetail',
                component: OrderDetailViewDetail
            },

            {
                path: '/payments',
                name: 'PaymentManager',
                component: PaymentManager
            },
            {
                path: '/payments/:id',
                name: 'PaymentDetail',
                component: PaymentDetail
            },

            {
                path: '/deliveries',
                name: 'DeliveryManager',
                component: DeliveryManager
            },
            {
                path: '/deliveries/:id',
                name: 'DeliveryDetail',
                component: DeliveryDetail
            },

            {
                path: '/deliveryStates',
                name: 'DeliveryStateView',
                component: DeliveryStateView
            },
            {
                path: '/deliveryStates/:id',
                name: 'DeliveryStateViewDetail',
                component: DeliveryStateViewDetail
            },


    ]
})
