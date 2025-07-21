<template>
  <van-nav-bar
    title="标签搜索结果"
    left-arrow
    @click-left="onClickLeft"
  />
  <user-card-list :user-list="userList" :loading="loading"/>
  <van-empty v-if="!loading && (!userList || userList.length < 1)" description="搜索结果为空" />
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute, useRouter} from "vue-router";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import qs from 'qs';
import UserCardList from "../components/UserCardList.vue";

const route = useRoute();
const router = useRouter();
const {tags} = route.query;

const userList = ref([]);
const loading = ref(true);

onMounted(async () => {
  if (!tags || (Array.isArray(tags) && tags.length === 0)) {
    Toast('请选择至少一个标签');
    setTimeout(() => {
      router.push('/search');
    }, 1000);
    return;
  }
  
  // 显示加载状态
  loading.value = true;
  Toast.loading({
    message: '搜索中...',
    forbidClick: true,
    duration: 0
  });
  
  const userListData = await myAxios.get('/user/search/tags', {
    params: {
      tagNameList: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params, {indices: false})
    }
  })
      .then(function (response) {
        console.log('/user/search/tags succeed', response);
        return response?.data;
      })
      .catch(function (error) {
        console.error('/user/search/tags error', error);
        Toast.fail('请求失败');
        return [];
      })
      .finally(() => {
        // 关闭加载提示
        Toast.clear();
        loading.value = false;
      });
  
  console.log(userListData)
  if (userListData) {
    userListData.forEach(user => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
});

const onClickLeft = () => {
  router.back();
};
</script>

<style scoped>
</style>
