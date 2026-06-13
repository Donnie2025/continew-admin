# 收藏功能 API 文档

## 数据库表结构

### edu_favorite 表
```sql
CREATE TABLE `edu_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID，关联edu_student表',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型（teacher-教师, material-教材）',
  `resource_id` bigint(20) NOT NULL COMMENT '资源ID（教师ID或教材ID）',
  `resource_name` varchar(100) DEFAULT NULL COMMENT '资源名称（教师姓名或教材名称）',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：有效；0：已取消）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（收藏时间）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_resource` (`student_id`, `resource_type`, `resource_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_resource_id` (`resource_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_student_type_status` (`student_id`, `resource_type`, `status`),
  CONSTRAINT `fk_favorite_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生收藏表';
```

## API 接口

### 1. 切换收藏状态（推荐使用）

**接口地址:** `POST /api/mini/favorite/toggle`

**功能描述:** 如果已收藏则取消，未收藏则添加

**请求参数:**
```json
{
  "studentId": 1,
  "resourceType": "teacher",
  "resourceId": 101,
  "resourceName": "Sarah Johnson"
}
```

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "isFavorited": true,
    "message": "收藏成功"
  }
}
```

### 2. 添加收藏

**接口地址:** `POST /api/mini/favorite/add`

**请求参数:**
```json
{
  "studentId": 1,
  "resourceType": "teacher",
  "resourceId": 101,
  "resourceName": "Sarah Johnson"
}
```

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "收藏成功",
  "data": null
}
```

### 3. 取消收藏

**接口地址:** `POST /api/mini/favorite/remove`

**请求参数:**
```json
{
  "studentId": 1,
  "resourceType": "teacher",
  "resourceId": 101
}
```

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "取消收藏成功",
  "data": null
}
```

### 4. 检查收藏状态

**接口地址:** `GET /api/mini/favorite/check`

**请求参数:**
- studentId: 学生ID
- resourceType: 资源类型（teacher/material）
- resourceId: 资源ID

**示例:** `/api/mini/favorite/check?studentId=1&resourceType=teacher&resourceId=101`

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "isFavorited": true
  }
}
```

### 5. 获取收藏列表

**接口地址:** `GET /api/mini/favorite/list`

**请求参数:**
- studentId: 学生ID
- resourceType: 资源类型（teacher/material）

**示例:** `/api/mini/favorite/list?studentId=1&resourceType=teacher`

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": [101, 102, 103]
}
```

### 6. 批量检查收藏状态

**接口地址:** `POST /api/mini/favorite/batch/check`

**请求参数:**
```json
{
  "studentId": 1,
  "resourceType": "teacher",
  "resourceIds": [101, 102, 103, 104, 105]
}
```

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "101": true,
    "102": false,
    "103": true,
    "104": false,
    "105": false
  }
}
```

### 7. 教师列表（已集成收藏状态）

**接口地址:** `GET /api/mini/teacher/active`

**请求参数:**
- name: 教师姓名（可选）
- startDate: 开课日期（可选，格式：YYYYMMDD）
- startTimeFrom: 开始时间（可选，格式：HH:MM）
- startTimeTo: 结束时间（可选，格式：HH:MM）
- page: 页码（默认1）
- pageSize: 每页数量（默认10）
- **studentId: 学生ID（可选，提供后会返回收藏状态）**

**示例:** `/api/mini/teacher/active?page=1&pageSize=10&studentId=1`

**响应示例:**
```json
{
  "success": true,
  "code": "200",
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 101,
        "name": "Sarah Johnson",
        "score": 5,
        "tags": "英语,四级,六级,口语",
        "avatar": "/avatars/sarah.jpg",
        "briefIntro": "资深英语教师",
        "description": "5年教学经验",
        "isFavorite": true
      },
      {
        "id": 102,
        "name": "Michael Brown",
        "score": 4,
        "tags": "托福,雅思",
        "avatar": "/avatars/michael.jpg",
        "briefIntro": "托福雅思专家",
        "description": "8年教学经验",
        "isFavorite": false
      }
    ],
    "total": 35,
    "page": 1,
    "pageSize": 10,
    "hasMore": true
  }
}
```

## 前端集成示例

### 修改教师列表页面

修改 `/pages/teacher/list.vue` 中的以下部分：

#### 1. 修改 onToggleFavorite 方法

```javascript
// 原本的本地存储方式
async onToggleFavorite(teacher) {
  try {
    const studentId = uni.getStorageSync('studentId') // 从本地存储获取学生ID
    
    const res = await http.post('/api/mini/favorite/toggle', {
      studentId: studentId,
      resourceType: 'teacher',
      resourceId: teacher.id,
      resourceName: teacher.name
    })
    
    if (res.success) {
      const isFavorited = res.data.isFavorite
      
      // 更新本地状态
      this.$set(teacher, 'isFavorite', isFavorited)
      const src = this.loadedTeachers.find(t => t.id === teacher.id)
      if (src) this.$set(src, 'isFavorite', isFavorited)
      
      uni.showToast({ 
        title: res.data.message, 
        icon: 'none' 
      })
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    uni.showToast({ 
      title: '操作失败，请重试', 
      icon: 'none' 
    })
  }
}
```

#### 2. 修改 loadTeachers 方法

```javascript
async loadTeachers(reset = true) {
  if (reset) {
    this.page = 1
    this.loadedTeachers = []
    this.loading = true
    showLoading('加载中...')
  } else {
    this.loadingMore = true
  }

  try {
    const studentId = uni.getStorageSync('studentId') // 获取学生ID
    
    const params = { 
      page: this.page, 
      pageSize: this.pageSize,
      studentId: studentId // 添加学生ID参数
    }
    
    if (this.keyword.trim()) params.name = this.keyword.trim()
    if (this.selectedDateIndex >= 0 && this.dateList[this.selectedDateIndex] && this.selectedStartTimeFrom) {
      const d = this.dateList[this.selectedDateIndex].dateObj
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const dd = String(d.getDate()).padStart(2, '0')
      params.startDate = `${y}${m}${dd}`
      params.startTimeFrom = this.selectedStartTimeFrom
      params.startTimeTo = this.selectedStartTimeTo || this.selectedStartTimeFrom
    }

    const res = await http.get('/api/mini/teacher/active', params)

    if (res.success && res.data && res.data.list) {
      const rawList = res.data.list
      this.total = res.data.total || 0
      this.hasMore = !!res.data.hasMore

      const teacherIds = rawList.map(t => t.id)
      const batchAvailableSlots = await this.getBatchTeacherAvailableSlots(teacherIds)

      const newItems = rawList.map(teacher => {
        const availableSlots = batchAvailableSlots[teacher.id] || { today: 0, tomorrow: 0 }
        const tagsArr = (teacher.tags ? String(teacher.tags).split(/[#,，]/) : []).map(t => t.trim()).filter(Boolean)
        const yearNum = Math.floor(Math.random() * 8) + 2
        const rating = (teacher.score && teacher.score >= 1) ? Number(teacher.score).toFixed(1) : (4.5 + Math.random() * 0.5).toFixed(1)
        return {
          id: teacher.id,
          name: teacher.name,
          avatar: teacher.avatar || '',
          score: rating,
          todaySlots: availableSlots.today || 0,
          tomorrowSlots: availableSlots.tomorrow || 0,
          tags: tagsArr,
          description: teacher.description || teacher.briefIntro || '专业教师，经验丰富',
          experience: `${yearNum}年教学经验`,
          specialty: this.getSpecialtyFromTags(teacher.tags),
          recentSlots: this.buildRecentSlots(availableSlots),
          isFavorite: teacher.isFavorite || false // 使用服务端返回的收藏状态
        }
      })

      this.loadedTeachers = [...this.loadedTeachers, ...newItems]
      this.applyQuickFilter()
    } else {
      // 处理错误情况
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
  } finally {
    this.loading = false
    this.loadingMore = false
    hideLoading()
  }
}
```

## 资源类型说明

- `teacher`: 教师
- `material`: 教材（未来扩展）

## 注意事项

1. 所有收藏操作都需要提供学生ID
2. 收藏采用软删除机制（status字段）
3. 唯一索引确保同一学生不会重复收藏同一资源
4. 删除学生时会级联删除其收藏记录（ON DELETE CASCADE）
5. 教师列表API需要传入studentId参数才会返回收藏状态
