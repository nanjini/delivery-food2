<template>

    <v-data-table
        :headers="headers"
        :items="deliveryState"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'DeliveryStateView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            deliveryState : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/deliveryStates'))

            temp.data._embedded.deliveryStates.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.deliveryState = temp.data._embedded.deliveryStates;
        },
        methods: {
        }
    }
</script>

