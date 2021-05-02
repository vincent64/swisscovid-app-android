package ch.admin.bag.dp3t.checkin.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.admin.bag.dp3t.databinding.ItemGenerateQrCodeBinding
import ch.admin.bag.dp3t.databinding.ItemQrCodeBinding
import ch.admin.bag.dp3t.databinding.ItemQrCodeEmptyListBinding
import org.crowdnotifier.android.sdk.model.v3.ProtoV3

class QrCodeAdapter(val onClickListener: OnClickListener) : RecyclerView.Adapter<QrCodeBaseViewHolder<*>>() {

	companion object {
		private const val TYPE_GENERATE_QR_CODE = 0
		private const val TYPE_QR_CODE = 1
		private const val TYPE_QR_EMPTY_LIST = 2
	}

	private var items: List<Any> = emptyList()

	fun setItems(qrCodes: List<ProtoV3.QRCodePayload>) {
		val newItems: ArrayList<Any> = ArrayList()
		newItems.add(GenerateQrCodeItem())
		if (qrCodes.isEmpty()) {
			newItems.add(QrCodeEmptyListItem())
		} else {
			newItems.addAll(qrCodes)
		}
		this.items = newItems
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QrCodeBaseViewHolder<*> {
		return when (viewType) {
			TYPE_GENERATE_QR_CODE -> {
				val binding = ItemGenerateQrCodeBinding
					.inflate(LayoutInflater.from(parent.context), parent, false)
				return GenerateQrCodeViewHolder(binding)
			}
			TYPE_QR_CODE -> {
				val binding = ItemQrCodeBinding
					.inflate(LayoutInflater.from(parent.context), parent, false)
				return QrCodeViewHolder(binding)
			}
			TYPE_QR_EMPTY_LIST -> {
				val binding = ItemQrCodeEmptyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				return QrCodeEmptyListViewHolder(binding)
			}
			else -> throw IllegalArgumentException("invalid view type")
		}
	}

	override fun onBindViewHolder(holder: QrCodeBaseViewHolder<*>, position: Int) {
		val item = items[position]
		when (holder) {
			is GenerateQrCodeViewHolder -> holder.bind(item as GenerateQrCodeItem)
			is QrCodeViewHolder -> holder.bind(item as ProtoV3.QRCodePayload)
			is QrCodeEmptyListViewHolder -> holder.bind(item as QrCodeEmptyListItem)
			else -> throw  IllegalArgumentException()
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when (items[position]) {
			is ProtoV3.QRCodePayload -> TYPE_QR_CODE
			is GenerateQrCodeItem -> TYPE_GENERATE_QR_CODE
			is QrCodeEmptyListItem -> TYPE_QR_EMPTY_LIST
			else -> throw java.lang.IllegalArgumentException("Invalid type of data at position $position")
		}
	}

	override fun getItemCount(): Int {
		return items.size
	}

	inner class GenerateQrCodeViewHolder(private val binding: ItemGenerateQrCodeBinding) :
		QrCodeBaseViewHolder<GenerateQrCodeItem>(binding.root) {
		override fun bind(item: GenerateQrCodeItem) {
			binding.qrCodeGenerate.setOnClickListener {
				onClickListener.generateQrCode()
			}
		}

	}

	inner class QrCodeViewHolder(private val binding: ItemQrCodeBinding) :
		QrCodeBaseViewHolder<ProtoV3.QRCodePayload>(binding.root) {
		override fun bind(item: ProtoV3.QRCodePayload) {
			binding.qrCodeName.text = item.locationData.description
			binding.qrCodeLocation.text = item.countryData.toString()
			binding.root.setOnClickListener { onClickListener.onQrCodeClicked(item) }
		}

	}

	inner class QrCodeEmptyListViewHolder(binding: ItemQrCodeEmptyListBinding) :
		QrCodeBaseViewHolder<QrCodeEmptyListItem>(binding.root) {
		override fun bind(item: QrCodeEmptyListItem) {

		}
	}
}

interface OnClickListener {
	fun generateQrCode()
	fun onQrCodeClicked(qrCodeItem: ProtoV3.QRCodePayload)
}


