<template>

    <v-data-table
        :headers="headers"
        :items="orderState"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'OrderStateView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            orderState : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/orderStates'))

            temp.data._embedded.orderStates.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.orderState = temp.data._embedded.orderStates;
        },
        methods: {
        }
    }
</script>

