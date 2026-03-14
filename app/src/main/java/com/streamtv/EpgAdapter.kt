package com.streamtv
import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.streamtv.databinding.ItemEpgBinding
class EpgAdapter(private val onClick: (EpgItem) -> Unit) : RecyclerView.Adapter<EpgAdapter.VH>() {
    private var items: List<EpgItem> = emptyList()
    fun submit(list: List<EpgItem>) { items = list; notifyDataSetChanged() }
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemEpgBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) { h.bind(items[pos]) { onClick(it) } }
    class VH(private val b: ItemEpgBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: EpgItem, click: (EpgItem) -> Unit) {
            b.tvTime.text = Scheduler.fmt(item.start)
            b.tvShow.text = item.show.title
            b.tvEp.text = item.episode.title
            b.root.alpha = if (item.isPast) 0.4f else 1f
            b.badgeNow.visibility = if (item.isNow) View.VISIBLE else View.GONE
            b.progress.visibility = if (item.isNow) View.VISIBLE else View.GONE
            if (item.isNow) b.progress.progress = item.progress
            b.root.setOnClickListener { click(item) }
        }
    }
}