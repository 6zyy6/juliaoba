<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入要搜索的标签"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签</div>
  <van-row gutter="16" style="padding: 0 16px">
    <van-col v-for="tag in activeIds" :key="tag">
      <van-tag closeable type="primary" @close="doClose(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />
  <div style="padding: 12px">
    <van-button block type="primary" @click="doSearchResult">搜索</van-button>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted} from 'vue';
import {useRouter} from "vue-router";
import {Toast} from "vant";
import myAxios from "../plugins/myAxios";

interface TagItem {
  text: string;
  id: string;
}

interface CategoryItem {
  text: string;
  children: TagItem[];
}

interface TagCategory {
  name: string;
  tags: string[];
}

const router = useRouter();

const searchText = ref('');

// 默认标签列表
const defaultTagList: CategoryItem[] = [{
  text: '加载中...',
  children: []
}];

// 标签列表
let tagList = ref<CategoryItem[]>(defaultTagList);
let originTagList = ref<CategoryItem[]>(defaultTagList);

// 获取标签分类
const fetchTagCategories = async () => {
  try {
    const res: any = await myAxios.get('/user/tags/categories');
    console.log('Tag categories response:', res);
    
    if (res.code === 0 && res.data) {
      // 转换为van-tree-select需要的格式
      const categories = res.data.map((category: TagCategory) => ({
        text: category.name,
        children: category.tags.map((tag: string) => ({
          text: tag,
          id: tag
        }))
      }));
      originTagList.value = categories;
      tagList.value = categories;
    } else {
      Toast.fail('获取标签分类失败');
    }
  } catch (error) {
    console.error('获取标签分类出错', error);
    Toast.fail('获取标签分类出错');
  }
};

onMounted(() => {
  fetchTagCategories();
});

/**
 * 搜索过滤
 * @param val 搜索文本
 */
const onSearch = (val: string) => {
  tagList.value = originTagList.value.map(parentTag => {
    const tempChildren = [...parentTag.children];
    const tempParentTag = {...parentTag};
    tempParentTag.children = tempChildren.filter(item => item.text.includes(searchText.value));
    return tempParentTag;
  });
}

const onCancel = () => {
  searchText.value = '';
  tagList.value = originTagList.value;
};

// 已选中的标签
const activeIds = ref<string[]>([]);
const activeIndex = ref(0);

// 移除标签
const doClose = (tag: string) => {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag;
  });
}

/**
 * 执行搜索
 */
const doSearchResult = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeIds.value
    }
  });
}
</script>

<style scoped>

</style>
