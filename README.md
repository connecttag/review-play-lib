# review-play-lib

مكتبة أندرويد عامة ومستقلة لتجريد وإدارة طلبات التقييم داخل التطبيق عبر **Google Play In-App Review API** وفق أفضل الممارسات والمعايير الذكية.

## المميزات
- تغليف آمن وتفاعلي لـ `ReviewManager` باستخدام Kotlin Coroutines.
- **إدارة ذكية لمعايير التقييم (`ReviewPromptCriteria`):**
  - تحديد الحد الأدنى لعدد الأيام منذ أول تثبيت (`minDaysSinceInstall`).
  - تحديد الحد الأدنى لمرات فتح وتشغيل التطبيق (`minAppLaunches`).
  - تحديد الحد الأدنى للأفعال والعمليات الإيجابية المكتملة (`minKeyActionsCount`).
  - فترة راحة بين الطلبات المتكررة لمنع إزعاج المستخدمين (`minDaysBetweenPrompts`).
- احترام وإدارة كوتا جوجل بلاي الصارمة (Google Play Quota).
- محرك محاكاة كامل (`FakeAppReviewEngine`) للاختبارات ووضع التطوير.
