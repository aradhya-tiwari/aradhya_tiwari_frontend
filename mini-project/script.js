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

let localStorageKey = Object.keys(localStorage)
let products = localStorageKey.map(e => localStorage.getItem(e))

function syncToLocalstorage() {
    for (let i in memProducts) {
        let curProd = memProducts[i]
        if (!localStorageKey.includes()) {
            localStorage.setItem(curProd.id, JSON.stringify(curProd))
        }
    }
    // So that new products will have their ids in localstorageKey
    localStorageKey = Object.keys(localStorage)
    products = localStorageKey.map(e => localStorage.getItem(e))
}
syncToLocalstorage()

function clearLocalstorage() {
    localStorage.clear()
}
const searchInput = document.getElementById("searchInput");
const categoryFilter = document.getElementById("categoryFilter");
const stockFilter = document.getElementById("stockFilter");
const sortFilter = document.getElementById("sortFilter");
const productList = document.getElementById("productList");
const resultsCount = document.getElementById("resultsCount");
const emptyState = document.getElementById("emptyState");

function populateCategories() {
    const categories = [...new Set(products.map((product) => product.category))];

    categories.forEach((category) => {
        const option = document.createElement("option");
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });
}
populateCategories()