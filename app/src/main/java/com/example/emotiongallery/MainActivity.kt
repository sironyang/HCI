package com.example.emotiongallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotiongallery.data.GalleryDatabase
import com.example.emotiongallery.data.GalleryPhotoEntity
import com.example.emotiongallery.data.PhotoRecordEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

data class GalleryPhoto(
    val id: Int,
    val title: String,
    val date: String,
    val place: String,
    val keywords: List<String>,
    val colors: List<Color>
)

data class PhotoRecord(
    val emotion: String = "",
    val memo: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmotionGalleryApp()
        }
    }
}

@Composable
fun EmotionGalleryApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF6F7FA)
    ) {
        GalleryRootScreen()
    }
}

@Composable
fun GalleryRootScreen() {
    val context = LocalContext.current
    val database = remember { GalleryDatabase.getDatabase(context) }
    val dao = database.galleryDao()
    val scope = rememberCoroutineScope()

    val photos by dao.getAllPhotos().collectAsState(initial = emptyList())
    val recordsFromDb by dao.getAllRecords().collectAsState(initial = emptyList())

    val records = remember(recordsFromDb) {
        mutableStateMapOf<Int, PhotoRecord>().apply {
            recordsFromDb.forEach { entity ->
                put(entity.photoId, PhotoRecord(entity.emotion, entity.memo))
            }
        }
    }

    LaunchedEffect(photos) {
        if (photos.isEmpty()) {
            val initialPhotos = listOf(
                GalleryPhotoEntity(title = "고양이", date = "2026/04/01", place = "집", keywords = listOf("고양이", "집", "귀여움", "평온"), colorStart = Color(0xFF8D6E63).toArgb(), colorEnd = Color(0xFFD7CCC8).toArgb()),
                GalleryPhotoEntity(title = "바다", date = "2026/04/03", place = "제주 바다", keywords = listOf("제주", "바다", "여행", "맑음", "평온"), colorStart = Color(0xFF64B5F6).toArgb(), colorEnd = Color(0xFFFFF59D).toArgb()),
                GalleryPhotoEntity(title = "노을", date = "2026/04/05", place = "해변", keywords = listOf("노을", "바다", "하늘", "기쁨"), colorStart = Color(0xFFFFB74D).toArgb(), colorEnd = Color(0xFFFFE0B2).toArgb()),
                GalleryPhotoEntity(title = "여행지", date = "2026/04/08", place = "휴양지", keywords = listOf("여행", "바다", "휴식", "평온"), colorStart = Color(0xFF4FC3F7).toArgb(), colorEnd = Color(0xFFC8E6C9).toArgb()),
                GalleryPhotoEntity(title = "아이스크림", date = "2026/04/12", place = "대전 유성구", keywords = listOf("아이스크림", "맑음", "공원", "봄", "기쁨"), colorStart = Color(0xFFFFCC80).toArgb(), colorEnd = Color(0xFFC5E1A5).toArgb()),
                GalleryPhotoEntity(title = "박스 속 고양이", date = "2026/04/14", place = "집", keywords = listOf("고양이", "박스", "귀여움", "기쁨"), colorStart = Color(0xFFA1887F).toArgb(), colorEnd = Color(0xFFFFECB3).toArgb()),
                GalleryPhotoEntity(title = "성당", date = "2026/04/17", place = "유럽 여행", keywords = listOf("성당", "건물", "여행"), colorStart = Color(0xFF90CAF9).toArgb(), colorEnd = Color(0xFFE3F2FD).toArgb()),
                GalleryPhotoEntity(title = "동굴", date = "2026/04/20", place = "관광지", keywords = listOf("동굴", "여행", "신기함"), colorStart = Color(0xFFBCAAA4).toArgb(), colorEnd = Color(0xFFD7CCC8).toArgb()),
                GalleryPhotoEntity(title = "말차 아이스크림", date = "2026/04/12", place = "대전 유성구", keywords = listOf("아이스크림", "말차", "봄", "공원", "기쁨"), colorStart = Color(0xFFAED581).toArgb(), colorEnd = Color(0xFFFFF9C4).toArgb()),
                GalleryPhotoEntity(title = "공원 아이스크림", date = "2026/04/13", place = "공원", keywords = listOf("아이스크림", "공원", "산책", "기쁨"), colorStart = Color(0xFFFFB74D).toArgb(), colorEnd = Color(0xFFFFF3E0).toArgb()),
                GalleryPhotoEntity(title = "길거리 간식", date = "2026/04/15", place = "시내", keywords = listOf("간식", "산책", "기쁨"), colorStart = Color(0xFFFFA726).toArgb(), colorEnd = Color(0xFFE1F5FE).toArgb())
            )
            dao.insertPhotos(initialPhotos)
        }
    }

    val galleryPhotos = remember(photos) {
        photos.map { entity ->
            GalleryPhoto(
                id = entity.id,
                title = entity.title,
                date = entity.date,
                place = entity.place,
                keywords = entity.keywords,
                colors = listOf(Color(entity.colorStart), Color(entity.colorEnd))
            )
        }
    }

    var selectedPhotoIndex by remember { mutableStateOf<Int?>(null) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    if (selectedPhotoIndex == null) {
        MainGalleryScreen(
            photos = galleryPhotos,
            records = records,
            searchText = searchText,
            onSearchChange = { searchText = it },
            onPhotoClick = { photo ->
                selectedPhotoIndex = galleryPhotos.indexOf(photo)
            }
        )
    } else {
        val currentIndex = selectedPhotoIndex ?: 0
        if (currentIndex >= galleryPhotos.size) {
            selectedPhotoIndex = null
            return
        }
        val photo = galleryPhotos[currentIndex]

        DetailPhotoScreen(
            photo = photo,
            record = records[photo.id] ?: PhotoRecord(),
            onBack = { selectedPhotoIndex = null },
            onPrevious = {
                selectedPhotoIndex = if (currentIndex == 0) {
                    galleryPhotos.lastIndex
                } else {
                    currentIndex - 1
                }
            },
            onNext = {
                selectedPhotoIndex = if (currentIndex == galleryPhotos.lastIndex) {
                    0
                } else {
                    currentIndex + 1
                }
            },
            onSaveMemo = { memo ->
                scope.launch {
                    val oldRecord = records[photo.id] ?: PhotoRecord()
                    val newRecord = oldRecord.copy(memo = memo)
                    dao.insertRecord(PhotoRecordEntity(photo.id, newRecord.emotion, newRecord.memo))
                }
            },
            onSaveEmotion = { emotion ->
                scope.launch {
                    val oldRecord = records[photo.id] ?: PhotoRecord()
                    val newRecord = oldRecord.copy(emotion = emotion)
                    dao.insertRecord(PhotoRecordEntity(photo.id, newRecord.emotion, newRecord.memo))
                }
            }
        )
    }
}

@Composable
fun MainGalleryScreen(
    photos: List<GalleryPhoto>,
    records: Map<Int, PhotoRecord>,
    searchText: TextFieldValue,
    onSearchChange: (TextFieldValue) -> Unit,
    onPhotoClick: (GalleryPhoto) -> Unit
) {
    val filteredPhotos = photos.filter { photo ->
        val record = records[photo.id]

        val searchableText = listOf(
            photo.title,
            photo.date,
            photo.place,
            photo.keywords.joinToString(" "),
            record?.memo.orEmpty(),
            record?.emotion.orEmpty()
        ).joinToString(" ")

        val query = searchText.text.trim()
        val queryWords = query.split("\\s+".toRegex()).filter { it.isNotBlank() }
        query.isBlank() || queryWords.all { word -> searchableText.contains(word, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderBar()

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "어떤 사진을 검색할까요?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(start = 36.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        SearchBox(
            value = searchText,
            onValueChange = onSearchChange,
            placeholder = "날짜,시간,감정 키워드 검색..."
        )

        Spacer(modifier = Modifier.height(40.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredPhotos) { photo ->
                PhotoThumbnail(
                    photo = photo,
                    record = records[photo.id],
                    onClick = { onPhotoClick(photo) }
                )
            }
        }
    }
}

@Composable
fun HeaderBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF7CB342), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "H",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "⌑",
            fontSize = 37.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SearchBox(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        leadingIcon = {
            Text(
                text = "⌕",
                color = Color(0xFF737783),
                fontSize = 21.sp
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF8E929E),
                fontSize = 14.sp
            )
        },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                Text(
                    text = "✕",
                    color = Color(0xFF737783),
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onValueChange(TextFieldValue("")) }
                )
            }
        },
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 15.sp
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEDEFF8),
            unfocusedContainerColor = Color(0xFFEDEFF8),
            disabledContainerColor = Color(0xFFEDEFF8),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        modifier = Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
fun PhotoThumbnail(
    photo: GalleryPhoto,
    record: PhotoRecord?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(photo.colors))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Text(
            text = photo.title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        if (!record?.emotion.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record!!.emotion.take(2),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPhotoScreen(
    photo: GalleryPhoto,
    record: PhotoRecord,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSaveMemo: (String) -> Unit,
    onSaveEmotion: (String) -> Unit
) {
    var showMemoPopup by remember { mutableStateOf(false) }
    var showEmotionSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderBar()

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .height(330.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(photo.colors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = photo.title,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "날짜:${photo.date}",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Text(
            text = "장소:${photo.place}",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )

        Text(
            text = if (record.memo.isBlank()) {
                "여기에 메모를 작성하거나\n감정을 기록하세요."
            } else {
                record.memo
            },
            fontSize = 24.sp,
            color = if (record.memo.isBlank()) Color(0xFF777777) else Color.Black,
            lineHeight = 30.sp,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .clickable { showMemoPopup = true }
        )

        if (record.emotion.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "기록된 감정: ${record.emotion}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        BottomActionBar(
            onBack = onBack,
            onMemo = { showMemoPopup = true },
            onEmotion = { showEmotionSheet = true },
            onNext = onNext
        )
    }

    if (showMemoPopup) {
        MemoPopup(
            initialMemo = record.memo,
            onDismiss = { showMemoPopup = false },
            onValueChange = { memo ->
                onSaveMemo(memo)
            }
        )
    }

    if (showEmotionSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showEmotionSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
        ) {
            EmotionSheet(
                selectedEmotion = record.emotion,
                onSelect = { emotion ->
                    onSaveEmotion(emotion)
                    showEmotionSheet = false
                }
            )
        }
    }
}

@Composable
fun BottomActionBar(
    onBack: () -> Unit,
    onMemo: () -> Unit,
    onEmotion: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "←",
            fontSize = 42.sp,
            color = Color.Black,
            modifier = Modifier.clickable { onBack() }
        )

        Text(
            text = "✎",
            fontSize = 38.sp,
            color = Color.Black,
            modifier = Modifier.clickable { onMemo() }
        )

        Text(
            text = "☺",
            fontSize = 46.sp,
            color = Color.Black,
            modifier = Modifier.clickable { onEmotion() }
        )

        Text(
            text = "→",
            fontSize = 42.sp,
            color = Color.Black,
            modifier = Modifier.clickable { onNext() }
        )
    }
}

@Composable
fun MemoPopup(
    initialMemo: String,
    onDismiss: () -> Unit,
    onValueChange: (String) -> Unit
) {
    var memoText by remember(initialMemo) {
        mutableStateOf(initialMemo)
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(500)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.42f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth()
                .height(230.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = "완료",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = memoText,
                    onValueChange = {
                        memoText = it
                        onValueChange(it)
                    },
                    placeholder = {
                        Text(
                            text = "메모를 직접 작성하세요",
                            color = Color(0xFF888888),
                            fontSize = 18.sp
                        )
                    },
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequester)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "닫기",
                            color = Color.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmotionSheet(
    selectedEmotion: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "감정 기록",
            fontSize = 31.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp, bottom = 26.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EmotionItem(
                label = "기쁨",
                emoji = "😃",
                selected = selectedEmotion == "기쁨",
                onClick = { onSelect("기쁨") }
            )

            EmotionItem(
                label = "평온",
                emoji = "😐",
                selected = selectedEmotion == "평온",
                onClick = { onSelect("평온") }
            )

            EmotionItem(
                label = "슬픔",
                emoji = "😭",
                selected = selectedEmotion == "슬픔",
                onClick = { onSelect("슬픔") }
            )

            EmotionItem(
                label = "화남",
                emoji = "😡",
                selected = selectedEmotion == "화남",
                onClick = { onSelect("화남") }
            )
        }
    }
}

@Composable
fun EmotionItem(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color(0xFF4CAF50) else Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = emoji,
            fontSize = 43.sp
        )
    }
}