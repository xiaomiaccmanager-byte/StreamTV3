package com.streamtv
import android.view.LayoutInflater; import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.streamtv.databinding.ItemChannelBinding
class ChannelAdapter(private val onClick: (Int) -> Unit) : RecyclerView.Adapter<ChannelAdapter.VH>() {
    private var items: List<Channel> = emptyList()
    private var active = 0
    fun submit(list: List<Channel>) { items = list; notifyDataSetChanged() }
    fun setActive(i: Int) { val old = active; active = i; notifyItemChanged(old); notifyItemChanged(i) }
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemChannelBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) { h.bind(items[pos], pos == active) { onClick(pos) } }
    class VH(private val b: ItemChannelBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(ch: Channel, isActive: Boolean, click: () -> Unit) {
            b.tvEmoji.text = ch.emoji
            b.tvName.text = ch.name
            b.tvNow.text = Scheduler.current(ch)?.show?.title ?: "Нет данных"
            b.tvNum.text = (adapterPosition + 1).toString().padStart(2, '0')
            b.root.setBackgroundColor(if (isActive) 0xFF18181F.toInt() else 0xFF0A0A0C.toInt())
            b.root.setOnClickListener { click() }
        }
    }
}