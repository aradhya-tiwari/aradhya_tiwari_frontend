// Sample products, used chatgpt for this products array 
const memProducts = [
    { id: 1, name: "Wireless Mouse", category: "Electronics", price: 25.99, stock: 12 },
    { id: 2, name: "Notebook", category: "Stationery", price: 4.5, stock: 30 },
    { id: 3, name: "Water Bottle", category: "Accessories", price: 12.75, stock: 4 },
    { id: 4, name: "Desk Lamp", category: "Furniture", price: 39.99, stock: 7 },
    { id: 5, name: "Ball Pen Set", category: "Stationery", price: 6.25, stock: 3 },
    { id: 6, name: "USB Keyboard", category: "Electronics", price: 18.99, stock: 9 },
    { id: 7, name: "Office Chair", category: "Furniture", price: 89.0, stock: 2 },
    { id: 8, name: "Laptop Sleeve", category: "Accessories", price: 21.49, stock: 6 }
];
const searchInput = document.getElementById("searchInput");
const categoryFilter = document.getElementById("categoryFilter");
const stockFilter = document.getElementById("stockFilter");
const sortFilter = document.getElementById("sortFilter");
const productList = document.getElementById("productList");
const resultsCount = document.getElementById("resultsCount");
const emptyState = document.getElementById("emptyState");

let localStorageKey = Object.keys(localStorage)
// since we are 
let products = localStorageKey.map(e => JSON.parse(localStorage.getItem(e)))
console.log("products", products)

function populateCategories() {
    const categories = [...new Set(products.map((product) => product.category))];

    categories.forEach((category) => {
        const option = document.createElement("option");
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });
}

function syncToLocalstorage() {
    for (let i in memProducts) {
        let curProd = memProducts[i]
        if (!localStorageKey.includes()) {
            localStorage.setItem(curProd.id, JSON.stringify(curProd))
        }
    }
    // So that new products will have their ids in localstorageKey
    localStorageKey = Object.keys(localStorage)
    // JSON parse here too, causing bug 
    products = localStorageKey.map(e => JSON.parse(localStorage.getItem(e)))
}

function clearLocalstorage() {
    localStorage.clear()
}

// function to filter products 
function getFilteredProducts() {
    const query = searchInput.value.trim().toLowerCase();
    const selectedCategory = categoryFilter.value;
    const selectedStock = stockFilter.value;
    const selectedSort = sortFilter.value;
    console.log(products)
    // filtered product 
    const filteredProducts = products.filter((product) => {
        // Checks whether the search query is included in the current product if no search query the it will be "" which is included in every product
        const matchesSearch = product.name.toLowerCase().includes(query);
        // checks if selectCategory is all, if not is the product.category equals selecteCategory
        const matchesCategory = selectedCategory === "all" || product.category === selectedCategory;
        // checks if selectedStock is all, if not is it less than 5 
        const matchesStock = selectedStock === "all" || product.stock < 5;
        // if all the criteria matches return true && is used which check if every case is true, (true true true than add item to filteredProduct)
        return matchesSearch && matchesCategory && matchesStock;
    });
    // after we get out filtered product we sort the items 
    filteredProducts.sort((firstProduct, secondProduct) => {
        if (selectedSort === "price-asc") {
            return firstProduct.price - secondProduct.price;
        }

        if (selectedSort === "price-desc") {
            return secondProduct.price - firstProduct.price;
        }

        if (selectedSort === "name-desc") {
            return secondProduct.name.localeCompare(firstProduct.name);
        }
        // otherwise return first product
        return firstProduct;
    });
    console.log("filtered products", filteredProducts)
    return filteredProducts;
}
// render products on dom, here we will insert products in productList's innerHTML which is unsafe
function renderProducts() {
    const filteredProducts = getFilteredProducts();

    productList.innerHTML = "";
    // show total results found from filteredProducts length
    resultsCount.textContent = `${filteredProducts.length} product${filteredProducts.length === 1 ? "" : "s"} found`;

    // initially emptyState is set to hidden which tailwind class to hide an element, if no element exists we need to show that element by removing hidden from emptyState's class
    if (filteredProducts.length === 0) {
        emptyState.classList.remove("hidden");
        return;
    }
    // if there are products we need to add hidden to emptyState so that it is not visible when there are products. we hardcoded it here to because it can change with every filter.
    emptyState.classList.add("hidden");

    filteredProducts.forEach((product) => {
        const card = document.createElement("article");
        // border for each car
        card.className = "rounded-2xl border border-slate-200 bg-white p-4 shadow-sm";
        // product component hard coded
        card.innerHTML = `
            <div class="mb-3 flex items-start justify-between gap-3">
                <div>
                    <h3 class="text-lg font-semibold text-slate-900">${product.name}</h3>
                    <p class="text-sm text-slate-500">${product.category}</p>
                </div>
                <span class="rounded-full px-3 py-1 text-xs font-medium ${product.stock < 5 ? "bg-amber-100 text-amber-800" : "bg-emerald-100 text-emerald-800"}">
                    ${product.stock < 5 ? "Low Stock" : "In Stock"}
                </span>
            </div>
            <div class="flex items-center justify-between text-sm text-slate-700">
                <span>Price: $${product.price.toFixed(2)}</span>
                <span>Stock: ${product.stock}</span>
            </div>
        `;
        // appending current product to productList otherwise it wont be visible in the DOM.
        productList.appendChild(card);
    });
}
// Add product function, first get all values from the form field using .value then push it to memProducts then call necessary functions, 
function addProduct() {
    let pName = document.getElementById("pName").value
    let pPrice = document.getElementById("pPrice").value
    let pStock = document.getElementById("pStock").value
    let pCategory = document.getElementById("pCategory").value
    memProducts.push({
        id: localStorageKey.length + 2,
        name: pName,
        category: pCategory,
        price: Number(pPrice),
        stock: Number(pStock),
    })
    console.log(memProducts)
    // To Sync changes to localstorage
    syncToLocalstorage()
    // to repo;ulate categories since we maybe adding new category
    populateCategories()
    // showing new product to product list
    renderProducts()
}

// We are adding event listner which will trigger if we write anything to input (search box) or change a select field
[searchInput, categoryFilter, stockFilter, sortFilter].forEach((control) => {
    control.addEventListener("input", renderProducts);
    control.addEventListener("change", renderProducts);
});

// Callling all the functions 
populateCategories()
syncToLocalstorage()
renderProducts()