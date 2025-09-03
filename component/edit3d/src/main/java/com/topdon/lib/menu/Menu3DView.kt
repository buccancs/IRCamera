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
 * 3D 编辑的菜单.
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
     * 视觉(0-3D、1-俯视、2-左视、3-右视、4-正视) 二级菜单切换事件监听.
     */
    var onVisualClickListener: ((position: Int) -> Unit)? = null
    /**
     * 标定(0-自定义、1-高温、2-低温、3-等温、4-删除) 二级菜单切换事件监听.
     */
    var onMarkClickListener: ((position: Int) -> Unit)? = null
    /**
     * 伪彩(0-铁红、1-黑红、2-自然、3-岩浆、4-辉金) 二级菜单切换事件监听.
     */
    var onPseudoClickListener: ((position: Int) -> Unit)? = null
    /**
     * 模式(0-点、1-线、2-面) 二级菜单切换事件监听.
     */
    var onModeClickListener: ((position: Int) -> Unit)? = null





    /**
     * 当前选中的一级菜单 index.
     */
    private var selectIndex = -1

    /**
     * 视觉(3D、俯视、左视、右视、正视) 二级菜单所用 Adapter.
     */
    private val visualAdapter: MenuAdapter
    /**
     * 标定(自定义、高温、低温、等温、删除) 二级菜单所用 Adapter.
     */
    private val markAdapter: MenuAdapter
    /**
     * 伪彩(铁红、黑红、自然、岩浆、辉金) 二级菜单所用 Adapter.
     */
    private val pseudoAdapter: MenuAdapter
    /**
     * 模式(点、线、面）二级菜单所用 Adapter.
     */
    private val modeAdapter: MenuAdapter


    /**
     * 文字选中时颜色值.
     */
    private val selectColor: Int = 0xffffffff.toInt()
    /**
     * 文字未选中时颜色值.
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