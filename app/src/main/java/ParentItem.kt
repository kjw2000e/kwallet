data class ParentItem(
    val id: Int,
    val title: String,
    var isChecked: Boolean = false,
    val children: MutableList<ChildItem> = mutableListOf(),
    var isExpanded: Boolean = false
)
