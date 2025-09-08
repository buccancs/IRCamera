package com.topdon.lib.menu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.utils.ScreenUtil

/**
 * 3D [Chinese text]menu.
 */
class Menu3DView : ConstraintLayout, View.OnClickListener {

    // View references - migrated from synthetic views
    private lateinit var viewMenu1Visual: View
    private lateinit var viewMenu1Mark: View
    private lateinit var viewMenu1Pseudo: View
    private lateinit var viewMenu1Mode: View
    private lateinit var ivMenu1Visual: ImageView
    private lateinit var tvMenu1Visual: TextView
    private lateinit var ivMenu1Mark: ImageView
    private lateinit var tvMenu1Mark: TextView
    private lateinit var ivMenu1Pseudo: ImageView
    private lateinit var tvMenu1Pseudo: TextView
    private lateinit var ivMenu1Mode: ImageView
    private lateinit var tvMenu1Mode: TextView
    private lateinit var recyclerView: RecyclerView

    /**
     * [Chinese text](0-3D, 1-[Chinese text], 2-[Chinese text], 3-[Chinese text], 4-[Chinese text]) [Chinese text]menuswitcheventlistener.
     */
    var onVisualClickListener: ((position: Int) -> Unit)? = null
    /**
     * [Chinese text](0-[Chinese text], 1-high[Chinese text], 2-low[Chinese text], 3-[Chinese text], 4-[Chinese text]) [Chinese text]menuswitcheventlistener.
     */
    var onMarkClickListener: ((position: Int) -> Unit)? = null
    /**
     * Pseudo color(0-[Chinese text], 1-[Chinese text], 2-[Chinese text], 3-[Chinese text], 4-[Chinese text]) [Chinese text]menuswitcheventlistener.
     */
    var onPseudoClickListener: ((position: Int) -> Unit)? = null
    /**
     * mode(0-point, 1-line, 2-[Chinese text]) [Chinese text]menuswitcheventlistener.
     */
    var onModeClickListener: ((position: Int) -> Unit)? = null

    /**
     * [Chinese text]in progress[Chinese text]menu index.
     */
    private var selectIndex = -1

    /**
     * [Chinese text](3D, [Chinese text], [Chinese text], [Chinese text], [Chinese text]) [Chinese text]menuused by Adapter.
     */
    private val visualAdapter: MenuAdapter
    /**
     * [Chinese text]([Chinese text], high[Chinese text], low[Chinese text], [Chinese text], [Chinese text]) [Chinese text]menuused by Adapter.
     */
    private val markAdapter: MenuAdapter
    /**
     * Pseudo color([Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]) [Chinese text]menuused by Adapter.
     */
    private val pseudoAdapter: MenuAdapter
    /**
     * mode(point, line, [Chinese text])[Chinese text]menuused by Adapter.
     */
    private val modeAdapter: MenuAdapter

    /**
     * text[Chinese text]in progress[Chinese text].
     */
    private val selectColor: Int = 0xffffffff.toInt()
    /**
     * text[Chinese text]in progress[Chinese text].
     */
    private val defaultColor: Int = 0x66ffffff

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes:Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        inflate(context, R.layout.view_menu_3d, this)
        setBackgroundColor(0xff16131e.toInt())
        
        // Initialize views - migrated from synthetic views
        initViews()

        viewMenu1Visual.setOnClickListener(this)
        viewMenu1Mark.setOnClickListener(this)
        viewMenu1Pseudo.setOnClickListener(this)
        viewMenu1Mode.setOnClickListener(this)

        visualAdapter = MenuAdapter(context, MenuAdapter.Type.VISUAL)
        markAdapter = MenuAdapter(context, MenuAdapter.Type.MARK)
        pseudoAdapter = MenuAdapter(context, MenuAdapter.Type.PSEUDO)
        modeAdapter = MenuAdapter(context, MenuAdapter.Type.MODE)
        visualAdapter.onItemClickListener = { onVisualClickListener?.invoke(it) }
        markAdapter.onItemClickListener = { onMarkClickListener?.invoke(it) }
        pseudoAdapter.onItemClickListener = { onPseudoClickListener?.invoke(it) }
        modeAdapter.onItemClickListener = { onModeClickListener?.invoke(it) }

        val orientation = if (ScreenUtil.isPortrait(context)) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        recyclerView.layoutManager = LinearLayoutManager(context, orientation, false)
        switchFirstMenu(0)
    }
    
    private fun initViews() {
        viewMenu1Visual = findViewById(R.id.view_menu1_visual)
        viewMenu1Mark = findViewById(R.id.view_menu1_mark)
        viewMenu1Pseudo = findViewById(R.id.view_menu1_pseudo)
        viewMenu1Mode = findViewById(R.id.view_menu1_mode)
        ivMenu1Visual = findViewById(R.id.iv_menu1_visual)
        tvMenu1Visual = findViewById(R.id.tv_menu1_visual)
        ivMenu1Mark = findViewById(R.id.iv_menu1_mark)
        tvMenu1Mark = findViewById(R.id.tv_menu1_mark)
        ivMenu1Pseudo = findViewById(R.id.iv_menu1_pseudo)
        tvMenu1Pseudo = findViewById(R.id.tv_menu1_pseudo)
        ivMenu1Mode = findViewById(R.id.iv_menu1_mode)
        tvMenu1Mode = findViewById(R.id.tv_menu1_mode)
        recyclerView = findViewById(R.id.recycler_view)
    }

    override fun onClick(v: View?) {
        when (v) {
            viewMenu1Visual -> switchFirstMenu(0)
            viewMenu1Mark -> switchFirstMenu(1)
            viewMenu1Pseudo -> switchFirstMenu(2)
            viewMenu1Mode -> switchFirstMenu(3)
        }
    }

    private fun switchFirstMenu(index: Int) {
        if (selectIndex == index) {
            return
        }
        when (selectIndex) {
            0 -> {
                ivMenu1Visual.isSelected = false
                tvMenu1Visual.setTextColor(defaultColor)
            }
            1 -> {
                ivMenu1Mark.isSelected = false
                tvMenu1Mark.setTextColor(defaultColor)
            }
            2 -> {
                ivMenu1Pseudo.isSelected = false
                tvMenu1Pseudo.setTextColor(defaultColor)
            }
            3 -> {
                ivMenu1Mode.isSelected = false
                tvMenu1Mode.setTextColor(defaultColor)
            }
        }
        when (index) {
            0 -> {
                ivMenu1Visual.isSelected = true
                tvMenu1Visual.setTextColor(selectColor)
                recyclerView.adapter = visualAdapter
            }
            1 -> {
                ivMenu1Mark.isSelected = true
                tvMenu1Mark.setTextColor(selectColor)
                recyclerView.adapter = markAdapter
            }
            2 -> {
                ivMenu1Pseudo.isSelected = true
                tvMenu1Pseudo.setTextColor(selectColor)
                recyclerView.adapter = pseudoAdapter
            }
            3 -> {
                ivMenu1Mode.isSelected = true
                tvMenu1Mode.setTextColor(selectColor)
                recyclerView.adapter = modeAdapter
            }
        }
        this.selectIndex = index
    }
}