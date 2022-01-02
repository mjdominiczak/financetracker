package com.mancode.financetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.mancode.financetracker.ui.reports.*
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
        binding.composeView.setContent {
            MdcTheme {
                val report by viewModel.report.observeAsState()
                val netValues by viewModel.netValues.observeAsState()
                val prevEnabled by viewModel.previousReportAvailable.observeAsState(false)
                val nextEnabled by viewModel.nextReportAvailable.observeAsState(false)
                val incomeByCat by viewModel.sumByIncomeCategories.observeAsState()
                val outcomeByCat by viewModel.sumByOutcomeCategories.observeAsState()
                if (report != null && !netValues.isNullOrEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
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
                        var activeTab by remember { mutableStateOf(0) }
                        CategoriesTabs(activeTab) { activeTab = it }
                        NetValuesForReport(
                            netValue1 = report!!.netValue1!!,
                            netValue2 = report!!.netValue2!!
                        )
                        when (activeTab) {
                            0 -> {
                                NetValueChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    netValues = netValues!!,
                                    reportRange = Pair(report!!.from, report!!.to)
                                )
                                TextReport(report = report!!)
                            }
                            1 -> CategoriesList(data = incomeByCat)
                            else -> CategoriesList(data = outcomeByCat)
                        }
                    }
                } else if (report == null || netValues == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text(text = stringResource(id = R.string.info_not_enough_data)) }
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