package com.example.myapplication

import android.os.Bundle
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BookApp()
                }
            }
        }
    }
}

data class Book(
    val id: Int,
    val title: String,
    val year: Int,
    val author: String,
    val genre: String,
    val status: BookStatus
)

enum class BookStatus {
    PLANNED,
    READING,
    DONE
}

val BookList = listOf(
    Book(id = 1, title = "Crime and Punishment", 1866, "Fyodor Dostoevsky", "Philosophical Fiction", BookStatus.DONE),
    Book(id = 2, title = "Pride and Prejudice", 1813, "Jane Austen", "Romance", BookStatus.READING),
    Book(id = 3, title = "War and Peace", 1869, "Leo Tolstoy", "Historical Fiction", BookStatus.PLANNED),
    Book(id = 4, title = "1984", 1949, "George Orwell", "Dystopian", BookStatus.READING),
    Book(id = 5, title = "The Master and Margarita", 1967, "Mikhail Bulgakov", "Fantasy", BookStatus.PLANNED),
    Book(id = 6, title = "To Kill a Mockingbird", 1960, "Harper Lee", "Southern Gothic", BookStatus.DONE),
    Book(id = 7, title = "The Great Gatsby", year = 1925, author = "F. Scott Fitzgerald", genre = "Tragedy", BookStatus.PLANNED),
    Book(id = 8, title = "One Hundred Years of Solitude", year = 1967, author = "Gabriel García Márquez", genre = "Magical Realism", BookStatus.READING),
    Book(id = 9, title = "Brave New World", year = 1932, author = "Aldous Huxley", genre = "Science Fiction", BookStatus.PLANNED),
    Book(id = 10, title = "The Catcher in the Rye", year = 1951, author = "J.D. Salinger", genre = "Literary Realism", BookStatus.DONE),
    Book(id = 11, title = "Moby-Dick", year = 1851, author = "Herman Melville", genre = "Adventure Fiction", BookStatus.READING),
    Book(id = 12, title = "The Odyssey", year = -800, author = "Homer", genre = "Epic Poetry", BookStatus.PLANNED),
    Book(id = 13, title = "Frankenstein", year = 1818, author = "Mary Shelley", genre = "Gothic Fiction", BookStatus.READING),
    Book(id = 14, title = "The Hobbit", year = 1937, author = "J.R.R. Tolkien", genre = "Fantasy",BookStatus.READING),
    Book(id = 15, title = "Jane Eyre", year = 1847, author = "Charlotte Brontë", genre = "Bildungsroman", BookStatus.PLANNED),
)

class BookStateHolder(
    initialBooks: List<Book>
){
    private var allBook by mutableStateOf(initialBooks)
    var searchQuery by mutableStateOf("")
    var selectedFilter by mutableStateOf<BookStatus?>(null)
    val filteredBook: List<Book>
        get() = allBook.filter { book ->
            val matchesSearch = searchQuery.isBlank() ||
                    book.title.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedFilter == null || book.status == selectedFilter
            matchesSearch && matchesFilter
        }
    val totalCount: Int
        get() = allBook.size
    val readingCount: Int
        get() = allBook.count {it.status == BookStatus.READING}
    val plannedCount: Int
        get() = allBook.count {it.status == BookStatus.PLANNED}
    val doneCount: Int
        get() = allBook.count {it.status == BookStatus.DONE}

    fun onSearchChange(newValue: String){
        searchQuery = newValue
    }
    fun onFilterChange(filter: BookStatus?) {
        selectedFilter = filter
    }
    fun onNextStatus(bookId: Int){
        allBook = allBook.map{ book ->
            if (book.id == bookId){
                book.copy(status = getNextStatus(book.status))
            } else{
                book
            }
        }
    }

    private fun getNextStatus(currentStatus: BookStatus): BookStatus {
        return when (currentStatus){
            BookStatus.PLANNED -> BookStatus.READING
            BookStatus.READING -> BookStatus.DONE
            BookStatus.DONE -> BookStatus.PLANNED
        }
    }
}

@Composable
fun BookApp(){
    val stateHolder = remember { BookStateHolder(BookList) }

    BookListScreen(
        bookList = stateHolder.filteredBook,
        searchQuery = stateHolder.searchQuery,
        onSearchChange = stateHolder::onSearchChange,
        onFilterChange = stateHolder::onFilterChange,
        selectedFilter = stateHolder.selectedFilter,
        totalCount = stateHolder.totalCount,
        readingCount = stateHolder.readingCount,
        plannedCount = stateHolder.plannedCount,
        doneCount = stateHolder.doneCount,
        onNextStatus = stateHolder::onNextStatus,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    bookList: List<Book>,
    searchQuery: String,
    selectedFilter: BookStatus?,
    totalCount: Int,
    readingCount: Int,
    plannedCount: Int,
    doneCount: Int,
    onSearchChange: (String) -> Unit,
    onFilterChange: (BookStatus?) -> Unit,
    onNextStatus: (Int) -> Unit,
){
    Scaffold(
        topBar = {
            TopAppBar(title = { Text( text= "Reader`s diary") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(paddingValues = innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text( text= "Search by title") },
                singleLine = true,
            )
            StatisticBlock(
                total = totalCount,
                planned = plannedCount,
                reading = readingCount,
                done = doneCount
            )

            FilterTabs(
                selectedFilter  = selectedFilter,
                onFilterSelected = onFilterChange
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            if (bookList.isEmpty()) {
                Text(text = "Ничего нет!")
            } else{
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy( space = 8.dp)
                ) {
                    items(items = bookList, key = { it.id }) { item ->
                        BookCard( book = item,  onNextStatus = { onNextStatus(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticBlock(
    total: Int,
    reading: Int,
    planned: Int,
    done: Int,
    modifier: Modifier = Modifier
){
    Surface( modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp) ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total $total")
            Text(text = "Reading $reading")
            Text(text = "Planned $planned")
            Text(text = "Done $done")
        }
    }
}

@Composable
fun FilterTabs(
    selectedFilter: BookStatus?,
    onFilterSelected: (BookStatus?) -> Unit,
    modifier: Modifier = Modifier
){
    val tabs = listOf<BookStatus?>(null) + BookStatus.entries
    val tabTitles = mapOf(
        null to "all",
        BookStatus.READING to "Reading",
        BookStatus.PLANNED to "Planned",
        BookStatus.DONE to "Done"
    )

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedFilter).coerceAtLeast(0),
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        tabs.forEach { status ->
            Tab(
                selected = selectedFilter == status,
                onClick = { onFilterSelected(status) },
                text = { Text(tabTitles[status]!!)}
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    onNextStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(all = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(text = book.title, fontWeight = FontWeight.Bold)
            Text(text = "${book.year} - ${book.genre}")
            Text(text = "Author: ${book.author}")
            Spacer(modifier = Modifier.height(4.dp))
            StatusBadge(status = book.status)
        }
        Button(
            onClick = onNextStatus,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(getNextStatusText(book.status))
        }
    }
}

@Composable
fun StatusBadge(status: BookStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        BookStatus.PLANNED -> Triple(
            Color(0xFFE3F2FD), // Light Blue
            Color(0xFF0D47A1), // Dark Blue
            "PLANNED"
        )
        BookStatus.READING -> Triple(
            Color(0xFFE8F5E9), // Light Green
            Color(0xFF1B5E20), // Dark Green
            "READING"
        )
        BookStatus.DONE -> Triple(
            Color(0xFFFFF3E0), // Light Orange
            Color(0xFFE65100), // Dark Orange
            "DONE"
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun getNextStatusText(status: BookStatus): String {
    return when (status) {
        BookStatus.PLANNED -> "START"
        BookStatus.READING -> "DONE"
        BookStatus.DONE -> "RESTART"
    }
}