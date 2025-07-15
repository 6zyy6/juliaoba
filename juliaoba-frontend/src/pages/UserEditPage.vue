<template>
  <template v-if="editUser.editKey !== 'tags'">
    <van-form @submit="onSubmit">
      <!-- 普通文本输入 -->
      <van-field
          v-if="editUser.editKey !== 'gender'"
          v-model="editUser.currentValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${editUser.editName}`"
      />
      
      <!-- 性别选择 -->
      <van-field
          v-if="editUser.editKey === 'gender'"
          v-model="genderLabel"
          is-link
          readonly
          name="gender"
          label="性别"
          placeholder="点击选择性别"
          @click="showGenderPicker = true"
      />
      <van-popup v-model:show="showGenderPicker" position="bottom">
        <van-picker
            :columns="genderColumns"
            @confirm="onGenderConfirm"
            @cancel="showGenderPicker = false"
        />
      </van-popup>
      
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </template>
  
  <!-- 标签编辑 - 单独处理 -->
  <template v-else>
    <div style="padding: 16px;">
      <van-field
          v-model="newTag"
          label="添加标签"
          placeholder="请输入标签名称"
          :rules="[{ pattern: /^.{1,15}$/, message: '标签长度在1-15个字符之间' }]"
      >
        <template #button>
          <van-button size="small" type="primary" @click="addTag">添加</van-button>
        </template>
      </van-field>
      
      <div style="margin-top: 16px;">
        <van-tag
            v-for="(tag, index) in tagList"
            :key="index"
            type="primary"
            closeable
            style="margin-right: 8px; margin-bottom: 8px;"
            @close="removeTag(index)"
        >
          {{ tag }}
        </van-tag>
      </div>
      
      <div style="margin-top: 16px;">
        <h3>推荐标签</h3>
        <van-tabs v-model:active="activeTagCategory">
          <van-tab v-for="(category, index) in tagCategories" :key="index" :title="category.name">
            <van-space wrap>
              <van-tag
                  v-for="(tag, tagIndex) in category.tags"
                  :key="tagIndex"
                  plain
                  type="primary"
                  style="margin-right: 8px; margin-bottom: 8px;"
                  @click="addRecommendTag(tag)"
              >
                {{ tag }}
              </van-tag>
            </van-space>
          </van-tab>
        </van-tabs>
      </div>
      
      <div style="margin: 16px;">
        <van-button round block type="primary" @click="submitTags">
          提交
        </van-button>
      </div>
    </div>
  </template>
</template>

<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import { ref, computed, onMounted } from "vue";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import {getCurrentUser} from "../services/user";

const route = useRoute();
const router = useRouter();

interface EditUser {
  editKey: string | undefined;
  currentValue: string | undefined;
  editName: string | undefined;
}

const editUser = ref<EditUser>({
  editKey: route.query.editKey as string | undefined,
  currentValue: route.query.currentValue as string | undefined,
  editName: route.query.editName as string | undefined,
});

// 性别选择相关
const showGenderPicker = ref(false);
const genderColumns = [
  { text: '男', value: 1 },
  { text: '女', value: 2 },
];

// 根据当前性别值计算显示的标签
const genderLabel = computed(() => {
  if (editUser.value.editKey === 'gender') {
    const genderValue = parseInt(editUser.value.currentValue as string);
    return genderValue === 1 ? '男' : genderValue === 2 ? '女' : '未设置';
  }
  return '';
});

// 性别选择确认
const onGenderConfirm = (item: { text: string; value: number }) => {
  editUser.value.currentValue = item.value.toString();
  showGenderPicker.value = false;
};

// 标签相关
const newTag = ref('');
const tagList = ref<string[]>([]);
const activeTagCategory = ref(0); // 默认选中第一个分类

// 标签分类
const tagCategories = ref<{name: string, tags: string[]}[]>([
  { name: '加载中...', tags: [] },
]);

// 获取标签分类
const fetchTagCategories = async () => {
  try {
    const res: any = await myAxios.get('/user/tags/categories');
    if (res.code === 0 && res.data) {
      tagCategories.value = res.data;
    } else {
      Toast.fail('获取标签分类失败');
    }
  } catch (error) {
    console.error('获取标签分类出错', error);
    Toast.fail('获取标签分类出错');
  }
};

// 初始化标签列表
onMounted(() => {
  if (editUser.value.editKey === 'tags' && editUser.value.currentValue) {
    try {
      const parsedTags = JSON.parse(editUser.value.currentValue);
      if (Array.isArray(parsedTags)) {
        tagList.value = parsedTags;
      }
    } catch (error) {
      console.error('解析标签失败', error);
    }
  }
  
  // 获取标签分类
  if (editUser.value.editKey === 'tags') {
    fetchTagCategories();
  }
});

// 添加标签
const addTag = () => {
  const tag = newTag.value.trim();
  if (!tag) {
    return;
  }
  
  // 检查标签长度
  if (tag.length > 15) {
    Toast('标签长度不能超过15个字符');
    return;
  }
  
  // 检查是否已存在相同标签
  if (tagList.value.includes(tag)) {
    Toast('该标签已存在');
    return;
  }
  
  // 限制最多添加10个标签
  if (tagList.value.length >= 10) {
    Toast('最多添加10个标签');
    return;
  }
  
  tagList.value.push(tag);
  newTag.value = '';
  updateTagsValue();
};

// 添加推荐标签
const addRecommendTag = (tag: string) => {
  // 检查是否已存在相同标签
  if (tagList.value.includes(tag)) {
    Toast('该标签已存在');
    return;
  }
  
  // 限制最多添加10个标签
  if (tagList.value.length >= 10) {
    Toast('最多添加10个标签');
    return;
  }
  
  tagList.value.push(tag);
  updateTagsValue();
};

// 移除标签
const removeTag = (index: number) => {
  tagList.value.splice(index, 1);
  updateTagsValue();
};

// 更新标签值
const updateTagsValue = () => {
  editUser.value.currentValue = JSON.stringify(tagList.value);
};

const onSubmit = async () => {
  const currentUser = await getCurrentUser();

  if (!currentUser) {
    Toast.fail('用户未登录');
    return;
  }

  console.log(currentUser, '当前用户')

  const res: any = await myAxios.post('/user/update', {
    'id': currentUser.id,
    [editUser.value.editKey as string]: editUser.value.currentValue,
  })
  console.log(res, '更新请求');
  if (res.code === 0 && res.data > 0) {
    Toast.success('修改成功');
    router.back();
  } else {
    Toast.fail('修改错误');
  }
};

// 提交标签
const submitTags = async () => {
  const currentUser = await getCurrentUser();

  if (!currentUser) {
    Toast.fail('用户未登录');
    return;
  }

  updateTagsValue();

  const res: any = await myAxios.post('/user/update', {
    'id': currentUser.id,
    'tags': editUser.value.currentValue,
  });
  console.log(res, '标签更新请求');
  if (res.code === 0 && res.data > 0) {
    Toast.success('标签修改成功');
    router.back();
  } else {
    Toast.fail('标签修改错误');
  }
};

</script>

<style scoped>

</style>
