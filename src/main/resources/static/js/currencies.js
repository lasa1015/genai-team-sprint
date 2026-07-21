// SAMPLE feature — the front-end half of the Currencies slice.
// Shows both halves of the pattern: READ (fetch the list) and WRITE (POST a new row).
// Copy this file as the template for your feature pages (rates.js, convert.js, ...).

let allCurrencies = [];

function currentFilter() {
  return document.getElementById("currency-filter").value.trim();
}

function matchesFilter(currency, filterText) {
  const haystack =
    `${currency.code} ${currency.name} ${currency.symbol ?? ""}`.toLowerCase();
  return haystack.includes(filterText.toLowerCase());
}

function renderCurrencies() {
  const rows = document.getElementById("rows");
  const status = document.getElementById("status");
  const filterText = currentFilter();

  if (allCurrencies.length === 0) {
    rows.innerHTML =
      '<tr><td colspan="3" class="status">No currencies found.</td></tr>';
    status.textContent = "No currencies are available in the database.";
    status.classList.remove("err");
    return;
  }

  const visibleCurrencies = filterText
    ? allCurrencies.filter((currency) => matchesFilter(currency, filterText))
    : allCurrencies;

  if (visibleCurrencies.length === 0) {
    rows.innerHTML =
      '<tr><td colspan="3" class="status">No currencies match that filter.</td></tr>';
    status.textContent = `No currencies match "${filterText}".`;
    status.classList.remove("err");
    return;
  }

  rows.innerHTML = visibleCurrencies
    .map(
      (c) => `
      <tr>
        <td class="mono">${c.code}</td>
        <td>${c.name}</td>
        <td class="sym">${c.symbol ?? ""}</td>
      </tr>`,
    )
    .join("");

  status.textContent = filterText
    ? `${visibleCurrencies.length} of ${allCurrencies.length} currencies shown.`
    : `${allCurrencies.length} currencies loaded from the database.`;
  status.classList.remove("err");
}

// --- READ: GET /api/currencies and render the table ---
async function loadCurrencies() {
  const rows = document.getElementById("rows");
  const status = document.getElementById("status");
  try {
    const res = await fetch("/api/currencies");
    if (!res.ok) throw new Error("HTTP " + res.status);
    allCurrencies = await res.json();

    renderCurrencies();
  } catch (err) {
    allCurrencies = [];
    rows.innerHTML =
      '<tr><td colspan="3" class="status err">Could not load currencies.</td></tr>';
    status.textContent =
      "Is the app running and the database seeded? Try /api/health/db. (" +
      err.message +
      ")";
    status.classList.add("err");
  }
}

// --- WRITE: POST /api/currencies with a JSON body, then re-read the list ---
async function addCurrency(event) {
  event.preventDefault(); // don't reload the page
  const form = event.target;
  const formStatus = document.getElementById("form-status");
  const body = {
    code: form.code.value.trim().toUpperCase(),
    name: form.name.value.trim(),
    symbol: form.symbol.value.trim(),
  };
  try {
    const res = await fetch("/api/currencies", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || "HTTP " + res.status); // show the API's 400 message
    }
    formStatus.textContent = `Added ${body.code}.`;
    formStatus.classList.remove("err");
    form.reset();
    loadCurrencies(); // the write is only "done" once the read shows it
  } catch (err) {
    formStatus.textContent = "Could not add: " + err.message;
    formStatus.classList.add("err");
  }
}

loadCurrencies();
document
  .getElementById("currency-filter")
  .addEventListener("input", renderCurrencies);
document.getElementById("add-form").addEventListener("submit", addCurrency);
