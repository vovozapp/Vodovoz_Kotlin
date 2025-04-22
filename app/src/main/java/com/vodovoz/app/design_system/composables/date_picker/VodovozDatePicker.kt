import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.yearMonth
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val russianLocale = Locale("ru")
private val selectedDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", russianLocale)
private val monthYearFormatter =
    DateTimeFormatter.ofPattern("LLLL yyyy", russianLocale).withLocale(russianLocale)

@Composable
fun VodovozCalendarDialog(
    initialDate: LocalDate = remember { LocalDate.now() },
    isSelectableDate: (LocalDate) -> Boolean = { true },
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var selection by remember { mutableStateOf(initialDate) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val coroutineScope = rememberCoroutineScope()

    val today = remember { LocalDate.now() }
    val currentMonth = remember { selection.yearMonth }
    val state = rememberCalendarState(
        startMonth = remember { currentMonth.minusYears(100) },
        endMonth = remember { currentMonth.plusYears(100) },
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )
    var yearPickerVisible by rememberSaveable { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.width(328.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.choose_date),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                    color = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 12.dp)
                )

                Text(
                    text = selection.format(selectedDateFormatter)?.capitalize(russianLocale)
                        ?: stringResource(id = R.string.space),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 36.dp,
                        end = 12.dp,
                        bottom = 8.dp
                    )
                )

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surface)

                CalendarHeader(
                    currentMonth = state.firstVisibleMonth.yearMonth,

                    goToPrevious = {
                        coroutineScope.launch {
                            val prevMonth = state.firstVisibleMonth.yearMonth.minusMonths(1)
                            state.animateScrollToMonth(prevMonth)
                        }
                    },
                    goToNext = {
                        coroutineScope.launch {
                            val nextMonth = state.firstVisibleMonth.yearMonth.plusMonths(1)
                            state.animateScrollToMonth(nextMonth)
                        }
                    },
                    onYearPickerButtonClicked = { yearPickerVisible = !yearPickerVisible }
                )
                Box {

                    Column {
                        DaysOfWeekTitle(daysOfWeek = daysOfWeek)

                        HorizontalCalendar(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(horizontal = 12.dp),
                            state = state,
                            dayContent = { day ->
                                Day(
                                    day = day,
                                    isSelected = selection == day.date,
                                    isDisabled = !isSelectableDate(day.date)
                                ) { clickedDay ->
                                    if (clickedDay.position == DayPosition.MonthDate && isSelectableDate(
                                            clickedDay.date
                                        )
                                    ) {
                                        selection = clickedDay.date
                                    }
                                }
                            }
                        )
                    }


                    androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier.matchParentSize(),
                        visible = yearPickerVisible,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp)
                            ) {
                                val years = (currentMonth.year - 100)..(currentMonth.year + 31)
                                val rows = years.chunked(3)

                                items(rows) { rowYears ->
                                    Row(
                                        modifier = Modifier.fillParentMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowYears.forEach { year ->
                                            Text(
                                                text = year.toString(),
                                                modifier = Modifier
                                                    .clip(MaterialTheme.shapes.small)
                                                    .padding(horizontal = 4.dp)
                                                    .then(
                                                        if (year == state.firstVisibleMonth.yearMonth.year) Modifier.background(
                                                            MaterialTheme.colorScheme.primary
                                                        ) else Modifier
                                                    )
                                                    .weight(1f)
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            state.scrollToMonth(
                                                                YearMonth.of(
                                                                    year,
                                                                    currentMonth.month
                                                                )
                                                            )
                                                            yearPickerVisible = false
                                                        }
                                                    }
                                                    .padding(vertical = 8.dp),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = when (year) {
                                                    state.firstVisibleMonth.yearMonth.year -> MaterialTheme.colorScheme.background
                                                    today.year -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.onBackground
                                                },
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp, end = 12.dp, start = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(id = R.string.cancel).uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            style = ExtendedTheme.typography.buttonSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onDateSelected(selection)
                            onDismiss()
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok).uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            style = ExtendedTheme.typography.buttonSmall
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun CalendarHeader(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
    onYearPickerButtonClicked: () -> Unit,
) {

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onYearPickerButtonClicked() }
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonth.format(monthYearFormatter).capitalize(russianLocale),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(24.dp)
                )
            }

            Row {
                IconButton(onClick = goToPrevious) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(onClick = goToNext) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}


@Suppress("NonSkippableComposable")
@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .wrapContentSize(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, russianLocale)
                    .capitalize(russianLocale),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    isDisabled: Boolean,
    onClick: (CalendarDay) -> Unit,
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(CircleShape)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )

            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when (day.position) {
                DayPosition.MonthDate -> when {
                    isSelected -> MaterialTheme.colorScheme.background
                    isDisabled -> MaterialTheme.colorScheme.onBackground.copy(0.38f)
                    else -> MaterialTheme.colorScheme.onBackground
                }

                else -> Color.Transparent
            },
            style = if (isSelected) MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            else MaterialTheme.typography.labelSmall
        )
    }
}

fun String.capitalize(locale: Locale): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}
