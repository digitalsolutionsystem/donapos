package com.doonapos.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doonapos.app.data.local.ConsignmentProductEntity
import com.doonapos.app.data.local.ProductEntity
import com.doonapos.app.ui.CartUiState
import com.doonapos.app.ui.components.CartView
import com.doonapos.app.ui.components.ProductCard
import com.doonapos.app.ui.theme.Ochre
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    products: List<ProductEntity>,
    consignmentProducts: List<ConsignmentProductEntity>,
    cartState: CartUiState,
    onAddToCart: (ProductEntity) -> Unit,
    onAddConsignmentToCart: (ConsignmentProductEntity) -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onUpdateQty: (com.doonapos.app.data.model.CartItem, Int) -> Unit,
    onClearCart: () -> Unit,
    onPayClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 720

    val categories = remember(products, consignmentProducts) {
        val cats = mutableListOf("Semua")
        cats.addAll(products.map { it.category }.distinct())
        if (consignmentProducts.isNotEmpty()) {
            cats.add("Titip Jual")
        }
        cats.distinct()
    }

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        if (selectedCategory == "Titip Jual") {
            emptyList()
        } else {
            products.filter { p ->
                val matchCat = selectedCategory == "Semua" || p.category.equals(selectedCategory, ignoreCase = true)
                val matchSearch = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true)
                matchCat && matchSearch
            }
        }
    }

    val filteredConsignment = remember(consignmentProducts, searchQuery, selectedCategory) {
        if (selectedCategory != "Semua" && selectedCategory != "Titip Jual") {
            emptyList()
        } else {
            consignmentProducts.filter { c ->
                searchQuery.isBlank() || c.name.contains(searchQuery, ignoreCase = true) || c.vendor.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var showPhoneCartSheet by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column: Catalog & Categories
        Column(
            modifier = Modifier
                .weight(if (isTablet) 1.6f else 1f)
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari produk atau menu...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    val isConsign = cat == "Titip Jual"
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            isSelected && isConsign -> Ochre
                            isSelected -> PineGreen
                            isConsign -> Color(0xFFFFF1D6)
                            else -> SurfaceContainerHigh
                        },
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected && isConsign -> Color.Black
                                isSelected -> Color.White
                                else -> Color.Black
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Catalog Grid
            Box(modifier = Modifier.weight(1f)) {
                if (filteredProducts.isEmpty() && filteredConsignment.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada produk ditemukan", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Regular products
                        items(filteredProducts, key = { "prd_${it.id}" }) { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = { onAddToCart(product) }
                            )
                        }

                        // Consignment products
                        items(filteredConsignment, key = { "csg_${it.id}" }) { cProduct ->
                            val entityAsProduct = ProductEntity(
                                id = cProduct.id,
                                name = "[Titip] ${cProduct.name}",
                                category = cProduct.category,
                                price = cProduct.price,
                                image = cProduct.image
                            )
                            ProductCard(
                                product = entityAsProduct,
                                onAddToCart = { onAddConsignmentToCart(cProduct) }
                            )
                        }
                    }
                }
            }

            // If phone, show floating cart bottom bar
            if (!isTablet && cartState.items.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PineGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable { showPhoneCartSheet = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${cartState.totalItemsCount} Item",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = Formatters.formatRupiah(cartState.totalAmount),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Right Column: Tablet Side Cart Panel
        if (isTablet) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                CartView(
                    cartState = cartState,
                    onCustomerNameChange = onCustomerNameChange,
                    onUpdateQty = onUpdateQty,
                    onClearCart = onClearCart,
                    onPayClick = onPayClick
                )
            }
        }
    }

    // Modal Bottom Sheet for Cart on Phones
    if (showPhoneCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhoneCartSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CartView(
                cartState = cartState,
                onCustomerNameChange = onCustomerNameChange,
                onUpdateQty = onUpdateQty,
                onClearCart = onClearCart,
                onPayClick = {
                    showPhoneCartSheet = false
                    onPayClick()
                },
                modifier = Modifier.fillMaxHeight(0.85f)
            )
        }
    }
}
