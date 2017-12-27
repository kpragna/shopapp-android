package com.shopify.ui.address.di

import com.domain.interactor.account.*
import com.shopify.interactor.checkout.GetCheckoutUseCase
import com.shopify.interactor.checkout.SetShippingAddressUseCase
import com.shopify.ui.address.contract.AddressListPresenter
import com.shopify.ui.address.contract.AddressPresenter
import dagger.Module
import dagger.Provides

@Module
class AddressModule {

    @Provides
    fun provideAddressPresenter(
            getCheckoutUseCase: GetCheckoutUseCase,
            setShippingAddressUseCase: SetShippingAddressUseCase,
            sessionCheckUseCase: SessionCheckUseCase,
            createCustomerAddressUseCase: CreateCustomerAddressUseCase,
            editCustomerAddressUseCase: EditCustomerAddressUseCase
    ): AddressPresenter = AddressPresenter(getCheckoutUseCase, setShippingAddressUseCase,
            sessionCheckUseCase, createCustomerAddressUseCase, editCustomerAddressUseCase)


    @Provides
    fun provideAddressListPresenter(
            getCustomerUseCase: GetCustomerUseCase,
            deleteCustomerAddressUseCase: DeleteCustomerAddressUseCase,
            setShippingAddressUseCase: SetShippingAddressUseCase
    ): AddressListPresenter
            = AddressListPresenter(getCustomerUseCase, deleteCustomerAddressUseCase, setShippingAddressUseCase)
}