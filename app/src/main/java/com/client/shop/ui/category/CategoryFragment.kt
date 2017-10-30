package com.client.shop.ui.category

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.client.shop.R
import com.client.shop.di.component.AppComponent
import com.client.shop.ui.base.ui.pagination.PaginationFragment
import com.client.shop.ui.base.ui.recycler.OnItemClickListener
import com.client.shop.ui.category.adapter.CategoryAdapter
import com.client.shop.ui.category.contract.CategoryPresenter
import com.client.shop.ui.category.contract.CategoryView
import com.client.shop.ui.category.di.CategoryModule
import com.client.shop.ui.details.DetailsActivity
import com.client.shop.ui.modal.SortBottomSheet
import com.domain.entity.Category
import com.domain.entity.Product
import com.domain.entity.SortType
import javax.inject.Inject

class CategoryFragment : PaginationFragment<Product, CategoryView, CategoryPresenter>(),
        CategoryView {

    @Inject lateinit var categoryPresenter: CategoryPresenter
    private lateinit var category: Category
    private var selectedSortType: SortType = SortType.NAME

    private val sortBottomSheet: SortBottomSheet by lazy {
        SortBottomSheet(context, object : OnItemClickListener<SortType> {
            override fun onItemClicked(data: SortType, position: Int) {
                if (selectedSortType != data) {
                    selectedSortType = data
                    onRefresh()
                }
            }
        })
    }

    companion object {

        private const val PER_PAGE = 10
        private const val EXTRA_CATEGORY = "EXTRA_CATEGORY"

        fun newInstance(category: Category): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    //ANDROID

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments.getParcelable(EXTRA_CATEGORY)
        val compatActivity = activity
        if (compatActivity is AppCompatActivity) {
            compatActivity.supportActionBar?.setDisplayShowTitleEnabled(true)
            compatActivity.supportActionBar?.title = category.title
        }
        setHasOptionsMenu(true)
        loadData(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_categories, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.sort) {
            sortBottomSheet.show(selectedSortType)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    //INIT

    override fun inject(component: AppComponent) {
        component.attachCategoryComponent(CategoryModule()).inject(this)
    }

    override fun createPresenter() = categoryPresenter

    override fun isGrid() = true

    //SETUP

    override fun setupAdapter() = CategoryAdapter(dataList, this)

    //LCE

    override fun loadData(pullToRefresh: Boolean) {
        super.loadData(pullToRefresh)
        presenter.loadProductList(PER_PAGE, paginationValue, category.id, selectedSortType)
    }

    override fun showContent(data: List<Product>) {
        super.showContent(data)
        if (data.isNotEmpty()) {
            paginationValue = data.last().paginationValue
            this.dataList.addAll(data)
            adapter?.notifyDataSetChanged()
        }
    }

    //CALLBACK

    override fun onItemClicked(data: Product, position: Int) {
        startActivity(DetailsActivity.getStartIntent(context, data.id))
    }
}