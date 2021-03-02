<template>
    <div class="subInput">
      <a-transfer
        :data-source="mockData"
        show-search
          :list-style="{
        width: '250px',
        height: '300px',
      }"
        :target-keys="targetKeys"
        :render="item => `${item.title}`"
        @change="handleChange"
      >
        <span slot="notFoundContent">
      没数据
    </span>
      </a-transfer>
    </div>
</template>

<script>
  export default {
    name: 'subInput',
    data() {
      return {
        mockData: [],
        targetKeys: []
      }
    },
    mounted () {
      this.getMock()
    },
    methods: {
      getMock() {
        const targetKeys = [];
        const mockData = [];
        for (let i = 0; i < 20; i++) {
          const data = {
            key: i.toString(),
            title: `AlertName${i + 1}`,
            // description: `description of content${i + 1}`,
            chosen: Math.random() * 2 > 1,
          };
          if (data.chosen) {
            targetKeys.push(data.key);
          }
          mockData.push(data);
        }
        this.mockData = mockData;
        this.targetKeys = [];
      },
      handleChange(targetKeys, direction, moveKeys) {
        console.log(targetKeys, direction, moveKeys);
        this.targetKeys = targetKeys;
      }
    }
  }
</script>

<style scoped>

</style>