//

//
// 监控记录
// @Route(path = RouterConfig.THERMAL_LOG)
// class MonitorLogActivity : BaseActivity() {
//
//    val adapter: MonitorLogAdapter by lazy { MonitorLogAdapter(this) }
//
//    override fun initContentView() = R.layout.activity_monitor_log
//
//    override fun initView() {
//        log_recycler.layoutManager = LinearLayoutManager(this)
//        log_recycler.adapter = adapter
//        adapter.listener = object : MonitorLogAdapter.OnItemClickListener {
//            override fun onClick(index: Int, thermalId: String) {
//                NavigationManager.getInstance().build(RouterConfig.THERMAL_LOG_CHART).navigation(baseContext)
//            }
//
//            override fun onLongClick(index: Int, thermalId: String) {
//
//            }
//
//        }
//    }
//
//    override fun initData() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val datas = AppDatabase.getInstance(baseContext).thermalDao()
//                .getThermalId(SharedManager.getUserId())
//            adapter.datas = datas as ArrayList<ThermalEntity>
//        }
//
//    }
// }
