package com.example.model

data class Team(
    val id: Int,
    val name: String,
    val emoji: String,
    val score: Int = 0,
    val streak: Int = 0,
    val themeIndex: Int = 0
)

enum class GameTab(val title: String, val icon: String, val subtitle: String) {
    TRIVIA("شبكة التحدي", "🧠", "أسئلة معرفية وتمكين نفسي"),
    MYSTERY_BOXES("صناديق المفاجآت", "🎁", "تحديات تمثيل ومواقف داعمة"),
    EMOTION_WHEEL("عجلة المشاعر", "🎡", "تعبير وجداني وكسر الجليد"),
    APPLAUSE_METER("مقياس الحماس", "👏", "تحدي التصفيق والطاقة الجماعية"),
    CALM_OASIS("واحة الهدوء", "🧘‍♀️", "تنفس جماعي وتوكيدات إيجابية")
}

data class TriviaQuestion(
    val id: String,
    val categoryId: Int,
    val points: Int,
    val question: String,
    val answer: String,
    val facilitatorTip: String,
    val isUsed: Boolean = false
)

data class TriviaCategory(
    val id: Int,
    val title: String,
    val emoji: String,
    val themeIndex: Int,
    val questions: List<TriviaQuestion>
)

enum class MysteryType(val badgeTitle: String, val emoji: String) {
    PANTOMIME("تحدي تمثيل المشاعر 🎭", "🎭"),
    GRATITUDE("رسالة امتنان وتقدير 💌", "💌"),
    SOCIAL_SCENARIO("موقف وقرار داعم 🤝", "🗣️"),
    BONUS_LUCK("مفاجأة الحظ والطاقة ⚡", "🌟"),
    GROUP_ENERGIZER("تحدي الحماس الجماعي 🔥", "🔥")
}

data class MysteryBoxItem(
    val number: Int,
    val emoji: String,
    val type: MysteryType,
    val prompt: String,
    val guidance: String,
    val points: Int,
    val isOpened: Boolean = false
)

data class WheelSlice(
    val id: Int,
    val label: String,
    val emoji: String,
    val prompt: String,
    val facilitatorNote: String,
    val points: Int,
    val colorIndex: Int
)

data class AffirmationCard(
    val id: Int,
    val title: String,
    val emoji: String,
    val affirmationText: String,
    val groupAction: String
)

enum class BreathingPhase(val label: String, val instruction: String, val durationSeconds: Int) {
    INHALE("شهيق عميق 🌸", "تنفسي ببطء من الأنف واشعري بالهدوء يملأ صدرك...", 4),
    HOLD("احتفاظ بالهواء ✨", "احتفظي بالنفس بهدوء وركزي على اللحظة الحالية...", 4),
    EXHALE("زفير مريح 🍃", "أخرجي الهواء ببطء من الفم واتركي أي توتر يرحل...", 6)
}

object DefaultGameContent {

    fun defaultTeams(): List<Team> = listOf(
        Team(id = 1, name = "فريق الياسمين", emoji = "🌷", score = 0, streak = 0, themeIndex = 0),
        Team(id = 2, name = "فريق الأمل", emoji = "🌟", score = 0, streak = 0, themeIndex = 1)
    )

    val availableTeamEmojis = listOf("🌷", "🌟", "🌸", "🦋", "💎", "🔥", "🌈", "🕊️", "👑", "🍀", "☀️", "💜")

    fun defaultCategories(): List<TriviaCategory> = listOf(
        TriviaCategory(
            id = 0,
            title = "الوعي بالمشاعر",
            emoji = "💗",
            themeIndex = 0,
            questions = listOf(
                TriviaQuestion(
                    id = "c0_q1",
                    categoryId = 0,
                    points = 100,
                    question = "ما هو الفرق بين كبت المشاعر وبين تنظيم المشاعر والتعبير عنها بطريقة صحية؟",
                    answer = "كبت المشاعر يعني تجاهلها وحبسها بالداخل مما يزيد التوتر، أما تنظيمها فيعني الاعتراف بها وفهم سببها والتعبير عنها بأمان.",
                    facilitatorTip = "اطلبي من الفريق ذكر مثال سريع لطريقة صحية للتعبير عن الغضب أو الحزن."
                ),
                TriviaQuestion(
                    id = "c0_q2",
                    categoryId = 0,
                    points = 200,
                    question = "اذكري 3 علامات جسدية ينبهنا بها الجسم عندما نشعر بالقلق أو الضغط النفسي؟",
                    answer = "تسارع نبضات القلب، شد العضلات (الكتفين أو الفك)، تغير التنفس، أو الشعور بالتعب واضطراب المعدة.",
                    facilitatorTip = "ذكّري المشاركات أن الانتباه لإشارات الجسد هو أول خطوة للعناية بالذات."
                ),
                TriviaQuestion(
                    id = "c0_q3",
                    categoryId = 0,
                    points = 300,
                    question = "ماذا نسمي القدرة على التحدث مع أنفسنا بلطف وتفهّم عند الوقوع في خطأ بدلاً من جلد الذات؟",
                    answer = "التعاطف مع الذات (Self-Compassion) أو الرفق بالنفس.",
                    facilitatorTip = "شجعي الفريق على صياغة جملة لطيفة نقولها لأنفسنا عندما نمر بيوم صعب."
                ),
                TriviaQuestion(
                    id = "c0_q4",
                    categoryId = 0,
                    points = 400,
                    question = "تحدي الفريق: حوّلوا هذه الفكرة السلبية «أنا دائماً أفشل في المهام الجديدة» إلى فكرة واقعية وداعمة نفسياً!",
                    answer = "«التجارب الجديدة تحتاج وقتاً للتعلم، وكل محاولة تكسبني خبرة وتقربني من النجاح».",
                    facilitatorTip = "تُسمى هذه المهارة بإعادة البناء المعرفي (تغيير زاوية التفكير)."
                )
            )
        ),
        TriviaCategory(
            id = 1,
            title = "مهارات التواصل",
            emoji = "🤝",
            themeIndex = 1,
            questions = listOf(
                TriviaQuestion(
                    id = "c1_q1",
                    categoryId = 1,
                    points = 100,
                    question = "ما هي أهم مهارة في «الاستماع الفعّال» التي تجعل المتحدث يشعر بالأمان والاهتمام؟",
                    answer = "الإنصات دون مقاطعة أو إصدار أحكام، مع التواصل البصري المريح وإظهار التفهم.",
                    facilitatorTip = "اسألي المشاركات: كيف تشعرين عندما يستمع إليكِ شخص بكل جوارحه؟"
                ),
                TriviaQuestion(
                    id = "c1_q2",
                    categoryId = 1,
                    points = 200,
                    question = "عند العتاب أو التعبير عن الانزعاج، لماذا يُفضّل استخدام «رسائل الأنا» (أنا شعرت بـ...) بدلاً من «أنتِ السبب»؟",
                    answer = "لأن «رسائل الأنا» تعبر عن المشاعر بوضوح دون مهاجمة الطرف الآخر، مما يفتح باب الحوار والتفاهم بدلاً من الدفاع.",
                    facilitatorTip = "اطلبي من الفريق تحويل جملة اتهام إلى رسالة «أنا أشعر...»."
                ),
                TriviaQuestion(
                    id = "c1_q3",
                    categoryId = 1,
                    points = 300,
                    question = "ما الفرق بين «التعاطف» (Empathy) وبين «الشفقة» عند مساندة صديقة تمر بأزمة؟",
                    answer = "التعاطف هو الشعور معها وتقدير قوتها والوقوف بجانبها باحترام، بينما الشفقة قد تشعرها بالضعف أو العجز.",
                    facilitatorTip = "أفضل جملة تعاطف: «أنا معكِ وأقدّر صعوبة ما تمرين به»."
                ),
                TriviaQuestion(
                    id = "c1_q4",
                    categoryId = 1,
                    points = 400,
                    question = "تحدي الحدود الصحية: كيف يمكن قول «لا» لطلب يفوق طاقتكِ بأسلوب لطيف وحازم في نفس الوقت؟",
                    answer = "«أقدّر ثقتكِ بي جداً، لكن جدول أعمالي/طاقتي حالياً لا تسمح لي بتقديم المساعدة بالشكل المطلوب».",
                    facilitatorTip = "وضع الحدود الصحية يحمي العلاقات من الإرهاق والاحتراق النفسي."
                )
            )
        ),
        TriviaCategory(
            id = 2,
            title = "المرونة النفسية",
            emoji = "🌱",
            themeIndex = 2,
            questions = listOf(
                TriviaQuestion(
                    id = "c2_q1",
                    categoryId = 2,
                    points = 100,
                    question = "تشبّه المرونة النفسية بنبات الخيزران أو النخلة في مواجهة الرياح؛ ماذا يعني ذلك في حياتنا؟",
                    answer = "الانحناء اللطيف للتكيف مع العواصف والصعوبات دون الانكسار، ثم استعادة التوازن والوقوف من جديد.",
                    facilitatorTip = "المرونة لا تعني عدم الشعور بالألم، بل القدرة على التعافي والنمو بعده."
                ),
                TriviaQuestion(
                    id = "c2_q2",
                    categoryId = 2,
                    points = 200,
                    question = "ما هي «دائرة التحكم» وكيف يساعدنا التركيز عليها في تخفيف التوتر اليومي؟",
                    answer = "هي التركيز على ما نملك تغييره (ردود أفعالنا، كلماتنا، خطواتنا) والتخفف من القلق المفرط بشأن ما لا نملك السيطرة عليه.",
                    facilitatorTip = "تمرين سريع: سمّوا شيئاً واحداً داخل دائرة تحكمكم اليوم."
                ),
                TriviaQuestion(
                    id = "c2_q3",
                    categoryId = 2,
                    points = 300,
                    question = "اذكري 3 عادات يومية بسيطة (" + "شواحن طاقة نفسية" + ") تساعد على تجديد النشاط وتخفيف الضغوط؟",
                    answer = "التنفس العميق، المشي أو الحركة، التحدث مع شخص داعم، كتابة الامتنان، أو أخذ استراحة قصيرة بدون شاشات.",
                    facilitatorTip = "امنحي 50 نقطة إضافية إذا شارك الفريق عادة مميزة يطبقونها فعلاً!"
                ),
                TriviaQuestion(
                    id = "c2_q4",
                    categoryId = 2,
                    points = 400,
                    question = "لماذا يعتبر «طلب المساعدة والدعم» عند الحاجة علامة قوة وشجاعة وليس علامة ضعف؟",
                    answer = "لأنه يعكس وعياً بالذات وثقة بالروابط الإنسانية، فالإنسان كائن اجتماعي يزدهر بالتكافل والتساند.",
                    facilitatorTip = "ذكّري المجموعة بمصادر الدعم الآمنة المتاحة في المجتمع."
                )
            )
        ),
        TriviaCategory(
            id = 3,
            title = "ألغاز ومرح جماعي",
            emoji = "🎉",
            themeIndex = 3,
            questions = listOf(
                TriviaQuestion(
                    id = "c3_q1",
                    categoryId = 3,
                    points = 100,
                    question = "لغز ترفيهي: شيء كلما أخذتِ منه كَبُر، وكلما أضفتِ إليه صَغُر.. فما هو؟ (ومعه تحدي ابتسامة جماعية!)",
                    answer = "الحفرة! 🌟 (والآن ابتسامة عريضة من كل عضوات الفريق لكسب النقاط كاملة!)",
                    facilitatorTip = "الضحك الجماعي يفرز هرمون الإندورفين ويكسر الحواجز فوراً."
                ),
                TriviaQuestion(
                    id = "c3_q2",
                    categoryId = 3,
                    points = 200,
                    question = "تحدي السرعة: في 20 ثانية فقط، اذكروا 6 أشياء في الحياة تبعث على الفرح والبهجة وتبدأ بحرف (الميم)!",
                    answer = "أمثلة: محبة، مطر، موسيقى، مرح، مشاركة، مفاجأة، مودة، مساعدة، مغامرة!",
                    facilitatorTip = "شغّلي العد التنازلي وشجعي الفريق الآخر على العد معهم بحماس!"
                ),
                TriviaQuestion(
                    id = "c3_q3",
                    categoryId = 3,
                    points = 300,
                    question = "لغز ذكاء: يمشي بلا رجلين، ويدخل الأذنين، ويحمل في طياته أجمل الكلمات والدعم.. فما هو؟",
                    answer = "الصوت / الكلمة الطيبة! 💬✨",
                    facilitatorTip = "الكلمة الطيبة جسر مباشر للطمأنينة النفسية."
                ),
                TriviaQuestion(
                    id = "c3_q4",
                    categoryId = 3,
                    points = 400,
                    question = "تحدي الإيقاع الجماعي: على الفريق تأليف شعار حماسي من سطرين عن الأمل والتعاون وترديده بصوت واحد مع حركة يد مميزة!",
                    answer = "أي شعار إبداعي يؤديه الفريق بروح واحدة وحماس عالٍ يستحق النقاط الكاملة!",
                    facilitatorTip = "التناغم الصوتي والحركي يعزز الشعور بالانتماء للمجموعة."
                )
            )
        )
    )

    fun defaultMysteryBoxes(): List<MysteryBoxItem> = listOf(
        MysteryBoxItem(
            number = 1,
            emoji = "🎭",
            type = MysteryType.PANTOMIME,
            prompt = "تحدي التمثيل الصامت: مثّلي بدون كلام شعور «شخص تلقّى خبراً مفرحاً بعد انتظار طويل» وعلى فريقك تخمينه خلال 30 ثانية!",
            guidance = "لغة الجسد وتعابير الوجه تعكس أكثر من 70% من مشاعرنا الحقيقية.",
            points = 200
        ),
        MysteryBoxItem(
            number = 2,
            emoji = "💌",
            type = MysteryType.GRATITUDE,
            prompt = "بطاقة امتنان: اختاري مشاركة من الفريق المنافس ووجهي لها كلمة تقدير صادقة على صفة جميلة لاحظتِها فيها اليوم!",
            guidance = "الامتنان المتبادل يحول المنافسة إلى مساحة دافئة من المودة والأمان النفسي.",
            points = 200
        ),
        MysteryBoxItem(
            number = 3,
            emoji = "🌟",
            type = MysteryType.BONUS_LUCK,
            prompt = "صندوق النور الذهبي! ✨ حصل فريقكم على 150 نقطة فورية مكافأة، بشرط أن يشارك كل عضو بكلمة واحدة تصف شعوره الجميل الآن!",
            guidance = "تسمية المشاعر الإيجابية يرسخها في الذاكرة الوجدانية.",
            points = 150
        ),
        MysteryBoxItem(
            number = 4,
            emoji = "🗣️",
            type = MysteryType.SOCIAL_SCENARIO,
            prompt = "موقف داعم: انضمت فتاة جديدة للمجموعة وتبدو خجولة ومنعزلة في الزاوية. اقترحوا 3 خطوات عملية ولطيفة لدمجها دون إحراجها!",
            guidance = "المبادرة بالترحيب اللطيف والسؤال المفتوح يزيل رهبة البدايات.",
            points = 250
        ),
        MysteryBoxItem(
            number = 5,
            emoji = "🔥",
            type = MysteryType.GROUP_ENERGIZER,
            prompt = "موجة الطاقة! على الفريقين الوقوف وعمل «موجة تشجيعية» بالأيدي من اليمين إلى اليسار مع صيحة تفاؤل عالية!",
            guidance = "الحركة الجسدية المشتركة تنشط الدورة الدموية وتجدد التركيز في الجلسة.",
            points = 200
        ),
        MysteryBoxItem(
            number = 6,
            emoji = "🎭",
            type = MysteryType.PANTOMIME,
            prompt = "تحدي البانتومايم: مثّلي دور «صديقة تهدّئ روع زميلتها المتوترة قبل امتحان أو مقابلة مهمة» بدون نطق أي كلمة!",
            guidance = "الحضور الهادئ والابتسامة المطمئنة ينقلان الأمان حتى قبل الكلام.",
            points = 200
        ),
        MysteryBoxItem(
            number = 7,
            emoji = "💎",
            type = MysteryType.GRATITUDE,
            prompt = "كنز القوة الداخلية: كل عضوة في الفريق تذكر مهارة أو صفة جميلة تفتخر بوجودها في شخصيتها!",
            guidance = "الاعتراف بنقاط القوة الذاتية ركيزة أساسية لتقدير الذات والتمكين.",
            points = 250
        ),
        MysteryBoxItem(
            number = 8,
            emoji = "🤝",
            type = MysteryType.SOCIAL_SCENARIO,
            prompt = "موقف وحكمة: حدث سوء فهم بين صديقتين بسبب رسالة نصية غامضة. ما هي أفضل نصيحة لإصلاح الموقف بحكمة؟",
            guidance = "الحوار المباشر وحسن الظن وتوضيح المقصد يذيب 90% من الخلافات.",
            points = 250
        ),
        MysteryBoxItem(
            number = 9,
            emoji = "🌈",
            type = MysteryType.GROUP_ENERGIZER,
            prompt = "تحدي العدس المكبّر للإيجابيات: اذكروا 4 فوائد أو دروس جميلة يمكن أن نتعلمها من المواقف الصعبة!",
            guidance = "النمو ما بعد التحديات يكشف لنا قدرات لم نكن نعلم بوجودها فينا.",
            points = 200
        ),
        MysteryBoxItem(
            number = 10,
            emoji = "🎁",
            type = MysteryType.BONUS_LUCK,
            prompt = "هدية المشاركة! 🎉 يحصل فريقكم على 200 نقطة، ويمكنكم إهداء 50 نقطة إضافية للفريق الآخر عربون روح رياضية!",
            guidance = "العطاء ومشاركة النجاح يضاعف الشعور بالسعادة لدى الطرفين.",
            points = 200
        ),
        MysteryBoxItem(
            number = 11,
            emoji = "🎯",
            type = MysteryType.SOCIAL_SCENARIO,
            prompt = "تحدي التوازن: عندما تتراكم المهام وتشعرين بالإرهاق، ما هي أول خطوة عملية تساعدكِ على استعادة ترتيب أولوياتك؟",
            guidance = "تجزئة المهام الكبيرة إلى خطوات صغيرة مع أخذ نفس عميق يزيل الشعور بالعجز.",
            points = 200
        ),
        MysteryBoxItem(
            number = 12,
            emoji = "👑",
            type = MysteryType.GROUP_ENERGIZER,
            prompt = "تاج التمكين: اختاروا عضوة من فريقكم لتقف في المنتصف، ويمطرها الفريق بـ 5 جمل تشجيعية حماسية في 20 ثانية!",
            guidance = "الدعم الجماعي المباشر يرفع الثقة بالنفس ويخلق ذكرى إيجابية لا تُنسى.",
            points = 300
        )
    )

    fun defaultWheelSlices(): List<WheelSlice> = listOf(
        WheelSlice(
            id = 0,
            label = "لحظة فخر",
            emoji = "🌟",
            prompt = "شاركي موقفاً أو تحدياً صغيراً نجحتِ في تجاوزه مؤخراً وشعرتِ بعده بالفخر بنفسك!",
            facilitatorNote = "احتفلي بالإنجازات اليومية البسيطة مهما بدت صغيرة.",
            points = 150,
            colorIndex = 0
        ),
        WheelSlice(
            id = 1,
            label = "ضحكة من القلب",
            emoji = "😂",
            prompt = "احكي موقفاً طريفاً أو نكتة خفيفة، أو قودي الفريق في تحدي «الضحكة المعدية» لمدة 15 ثانية!",
            facilitatorNote = "المرح يكسر التوتر ويعيد شحن طاقة القاعة بالكامل.",
            points = 150,
            colorIndex = 1
        ),
        WheelSlice(
            id = 2,
            label = "نقطة قوتي",
            emoji = "💪",
            prompt = "لو كانت لديكِ «قوة خارقة نفسية» تستخدمينها لمساندة من حولك (مثل الصبر، الحنان، التفاؤل، الحكمة)، فماذا ستكون؟",
            facilitatorNote = "ربط الصفات الشخصية بمفهوم القوة يعزز الهوية الإيجابية.",
            points = 150,
            colorIndex = 2
        ),
        WheelSlice(
            id = 3,
            label = "رسالة شكر",
            emoji = "💐",
            prompt = "وجهي رسالة شكر لشخص (داخل القاعة أو في حياتك) كان له أثر طيب وداعم في أيامك!",
            facilitatorNote = "تذكر الأشخاص الداعمين يعزز الشعور بالأمان والارتباط الاجتماعي.",
            points = 150,
            colorIndex = 3
        ),
        WheelSlice(
            id = 4,
            label = "تغيير الزاوية",
            emoji = "🌈",
            prompt = "أكملي الجملة: «عندما يغلق باب، فإنني أتعلم كيف...» بكلمات تبعث على الأمل والمرونة!",
            facilitatorNote = "التفكير المرن يفتح مسارات جديدة للحلول.",
            points = 200,
            colorIndex = 0
        ),
        WheelSlice(
            id = 5,
            label = "تحدي 30 ثانية",
            emoji = "⚡",
            prompt = "في 30 ثانية: عدّدوا كفريق 7 أشياء بسيطة في يومكم تجلب لكم السكينة والراحة النفسية!",
            facilitatorNote = "الالتفات للتفاصيل الجميلة اليومية يغذي الشعور بالرضا.",
            points = 200,
            colorIndex = 1
        ),
        WheelSlice(
            id = 6,
            label = "تقمص الدور",
            emoji = "🎭",
            prompt = "تخيلي أن صديقتكِ المقربة تقول: «أشعر أنني وحيدة ولا أحد يفهمني».. ماذا ستقولين لها في جملتين من القلب؟",
            facilitatorNote = "تدريب عملي على الاحتواء العاطفي والتواجد الداعم.",
            points = 200,
            colorIndex = 2
        ),
        WheelSlice(
            id = 7,
            label = "هدف مشرق",
            emoji = "🎯",
            prompt = "شاركي عادة إيجابية صغيرة تودين البدء بها هذا الأسبوع للعناية بصحتكِ النفسية أو الجسدية!",
            facilitatorNote = "الإعلان عن النية أمام مجموعة داعمة يزيد الالتزام والتحفيز.",
            points = 150,
            colorIndex = 3
        )
    )

    fun defaultAffirmations(): List<AffirmationCard> = listOf(
        AffirmationCard(
            id = 1,
            title = "قيمة الذات والأمان",
            emoji = "🌸",
            affirmationText = "«أنا أستحق الاحترام والتقدير والسلام الداخلي، ومشاعري مهمة ومسموعة»",
            groupAction = "رددوا التوكيد معاً بصوت هادئ وواثق مع وضع اليد اليمنى على القلب."
        ),
        AffirmationCard(
            id = 2,
            title = "القدرة على التجاوز",
            emoji = "🌱",
            affirmationText = "«لقد تجاوزت أياماً صعبة من قبل، ولديّ من القوة والمرونة ما يعينني على النمو دائماً»",
            groupAction = "خذوا نفساً عميقاً وابتسموا لبعضكم البعض بتقدير."
        ),
        AffirmationCard(
            id = 3,
            title = "قوة التساند الجماعي",
            emoji = "🤝",
            affirmationText = "«نحن معاً أقوى؛ كلماتنا اللطيفة ومساندتنا لبعضنا تصنع مساحة آمنة ومزهرة»",
            groupAction = "تحية عالية (High-Five) أو تصفيقة دافئة لكل الحاضرات في القاعة!"
        ),
        AffirmationCard(
            id = 4,
            title = "الرفق بالنفس",
            emoji = "🕊️",
            affirmationText = "«أسمح لنفسي بالتعلم خطوة بخطوة، وأعامل ذاتي باللطف والصبر كل يوم»",
            groupAction = "أغمضوا الأعين لثلاث ثوانٍ واستشعروا الامتنان لوجودكم اليوم."
        ),
        AffirmationCard(
            id = 5,
            title = "الأمل والإشراق",
            emoji = "☀️",
            affirmationText = "«كل يوم جديد يحمل فرصة جديدة لبداية أجمل، وأنا أختار الأمل والخطوات الإيجابية»",
            groupAction = "شاركوا كلمة واحدة تلخص أجمل شعور خرجتم به من جلسة اليوم."
        )
    )
}
