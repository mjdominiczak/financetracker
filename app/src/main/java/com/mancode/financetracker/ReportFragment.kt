package com.mancode.financetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.composethemeadapter.MdcTheme
import com.mancode.financetracker.databinding.FragmentReportMonthlyBinding
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.ui.reports.NetValueChart
import com.mancode.financetracker.ui.reports.NetValuesForReport
import com.mancode.financetracker.ui.reports.ReportButtons
import com.mancode.financetracker.ui.reports.TextReport
import com.mancode.financetracker.viewmodel.ReportViewModel

class ReportFragment : Fragment(R.layout.fragment_report_monthly) {

    private var _binding: FragmentReportMonthlyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { dismiss() }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        binding.composeView.setContent {
            MdcTheme {
                val report by viewModel.report.observeAsState()
                val netValues by viewModel.netValues.observeAsState()
                val prevEnabled by viewModel.previousReportAvailable.observeAsState(false)
                val nextEnabled by viewModel.nextReportAvailable.observeAsState(false)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (report != null && !netValues.isNullOrEmpty()) {
                        ReportButtons(
                            date1 = report!!.from,
                            date2 = report!!.to,
                            leftEnabled = prevEnabled,
                            rightEnabled = nextEnabled,
                            onLeftClick = {
                                viewModel.requestPreviousReport()
                            },
                            onMiddleClick = {
                                viewModel.requestActualReport()
                            },
                            onRightClick = {
                                viewModel.requestNextReport()
                            }
                        )
                        NetValuesForReport(
                            netValue1 = report!!.netValue1,
                            netValue2 = report!!.netValue2
                        )
                        NetValueChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            netValues = netValues!!,
                            reportRange = Pair(report!!.from, report!!.to)
                        )
                        TextReport(report = report!!)
                    } else {
                        Text(text = stringResource(id = R.string.info_not_enough_data))
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun dismiss() {
        this.hideKeyboard()
        NavHostFragment.findNavController(this).navigateUp()
    }
}