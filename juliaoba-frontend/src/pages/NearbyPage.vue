<template>
  <div id="nearbyPage">
    <van-nav-bar
      title="附近的人"
      left-arrow
      @click-left="onClickLeft"
    />
    <van-notice-bar
      v-if="locationTips"
      :text="locationTips"
      mode="closeable"
    />
    <van-empty v-if="!loading && (!userList || userList.length < 1)" description="附近没有用户"/>
    <template v-else>
      <van-cell-group>
        <van-slider
          v-model="searchRadius"
          :min="1"
          :max="20"
          :step="1"
          @change="searchRadiusChanged"
        >
          <template #button>
            <div class="custom-button">{{ searchRadius }}km</div>
          </template>
        </van-slider>
      </van-cell-group>
      <user-card-list :user-list="userList" :loading="loading" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Toast } from 'vant';
import UserCardList from '../components/UserCardList.vue';
import myAxios from '../plugins/myAxios';
import { UserType } from '../models/user';

const router = useRouter();
const loading = ref(true);
const userList = ref<UserType[]>([]);
const locationTips = ref('');
const searchRadius = ref(5); // 默认5千米

// 获取用户位置
const getUserLocation = (): Promise<GeolocationPosition> => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('您的浏览器不支持地理定位'));
      return;
    }

    navigator.geolocation.getCurrentPosition(resolve, reject, {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    });
  });
};

// 更新用户位置到服务器
const updateUserLocation = async (latitude: number, longitude: number) => {
  try {
    await myAxios.post('/location/update', {
      latitude,
      longitude
    });
    console.log('位置更新成功');
    return true;
  } catch (error) {
    console.error('位置更新失败:', error);
    return false;
  }
};

// 获取附近的用户
const getNearbyUsers = async (distance: number = 5) => {
  loading.value = true;
  try {
    const response = await myAxios.get('/location/nearby', {
      params: { distance }
    });
    
    if (response.code === 0 && response.data) {
      userList.value = response.data.map((user: any) => {
        // 解析标签
        if (user.tags) {
          user.tags = JSON.parse(user.tags);
        }
        
        // 添加距离描述
        user.profile = `距离: ${user.distance}km`;
        return user;
      });
    } else {
      Toast.fail('获取附近用户失败');
    }
  } catch (error) {
    console.error('获取附近用户错误:', error);
    Toast.fail('获取附近用户出错');
  } finally {
    loading.value = false;
  }
};

// 搜索半径变化时
const searchRadiusChanged = (value: number) => {
  searchRadius.value = value;
  getNearbyUsers(value);
};

onMounted(async () => {
  try {
    // 请求位置权限并获取用户当前位置
    const position = await getUserLocation();
    const { latitude, longitude } = position.coords;
    
    // 更新用户位置到服务器
    const updateSuccess = await updateUserLocation(latitude, longitude);
    
    if (updateSuccess) {
      // 获取附近用户
      await getNearbyUsers(searchRadius.value);
    } else {
      locationTips.value = '位置更新失败，请稍后重试';
    }
  } catch (error) {
    console.error('位置获取失败:', error);
    locationTips.value = '无法获取您的位置，请允许位置访问并刷新页面';
    loading.value = false;
  }
});

const onClickLeft = () => {
  router.back();
};
</script>

<style scoped>
#nearbyPage {
  padding-bottom: 50px;
}
.custom-button {
  width: 40px;
  color: #fff;
  font-size: 10px;
  line-height: 18px;
  text-align: center;
  background-color: #1989fa;
  border-radius: 100px;
}
</style> 