<template>
  <div id="teamAddPage">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="addTeamData.name"
            name="name"
            label="队伍名"
            placeholder="请输入队伍名"
            :rules="[{ required: true, message: '请输入队伍名' }]"
        />
        <van-field
            v-model="addTeamData.description"
            rows="4"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
        />
        <van-field
            is-link
            readonly
            name="datetimePicker"
            label="过期时间"
            :placeholder="addTeamData.expireTime ? formatDate(addTeamData.expireTime) : '点击选择过期时间'"
            @click="showPicker = true"
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-datetime-picker
              v-model="tempExpireTime"
              @confirm="onConfirmDate"
              @cancel="showPicker = false"
              type="datetime"
              title="请选择过期时间"
              :min-date="minDate"
          />
        </van-popup>
        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="addTeamData.maxNum" max="10" min="3"/>
          </template>
        </van-field>
        <van-field name="radio" label="队伍状态">
          <template #input>
            <van-radio-group v-model="addTeamData.status" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <van-radio name="1">私有</van-radio>
              <van-radio name="2">加密</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(addTeamData.status) === 2"
            v-model="addTeamData.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入队伍密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";

const router = useRouter();
// 展示日期选择器
const showPicker = ref(false);

// 临时存储日期选择器的值
const tempExpireTime = ref(new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)); // 默认一周后

interface TeamFormData {
  name: string;
  description: string;
  expireTime: Date | null;
  maxNum: number;
  password: string;
  status: string | number;
}

const initFormData: TeamFormData = {
  "name": "",
  "description": "",
  "expireTime": null,
  "maxNum": 3,
  "password": "",
  "status": 0,
}

const minDate = new Date();

// 需要用户填写的表单数据
const addTeamData = ref({...initFormData})

// 格式化日期显示
const formatDate = (date: Date | string | null): string => {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const minute = String(d.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hour}:${minute}`;
};

// 确认日期选择
const onConfirmDate = (value: Date): void => {
  addTeamData.value.expireTime = value;
  showPicker.value = false;
};

// 提交
const onSubmit = async () => {
  // 表单验证
  if (!addTeamData.value.name) {
    Toast.fail('请输入队伍名称');
    return;
  }
  
  if (!addTeamData.value.expireTime) {
    Toast.fail('请选择过期时间');
    return;
  }
  
  // 如果是加密队伍，必须设置密码
  if (Number(addTeamData.value.status) === 2 && !addTeamData.value.password) {
    Toast.fail('加密队伍必须设置密码');
    return;
  }
  
  const postData = {
    ...addTeamData.value,
    status: Number(addTeamData.value.status)
  }
  
  console.log('提交的队伍数据:', postData);
  
  try {
    const res: any = await myAxios.post("/team/add", postData);
    console.log('服务器响应:', res);
    
    if (res?.code === 0 && res.data){
      Toast.success('添加成功');
      router.push({
        path: '/team',
        replace: true,
      });
    } else {
      Toast.fail(`添加失败: ${res?.description || '未知错误'}`);
    }
  } catch (error) {
    console.error('添加队伍出错:', error);
    Toast.fail('网络错误，请稍后重试');
  }
}
</script>

<style scoped>
#teamPage {

}
</style>
