async function loadRates() {
  const rows = document.getElementById("rate-rows");
  const status = document.getElementById("rates-status");
  const pairSelect = document.getElementById("pair-select");
  const pairRows = document.getElementById("pair-lookup-result");
  const pairStatus = document.getElementById("pair-status");

  try {
    const res = await fetch("/api/rates");
    if (!res.ok) throw new Error("HTTP " + res.status);

    const rates = await res.json();
    if (rates.length === 0) {
      rows.innerHTML =
        '<tr><td colspan="4" class="status">No rates found.</td></tr>';
      status.textContent = "No FX rates are available in the database.";
      status.classList.remove("err");
      pairSelect.innerHTML = "<option>No pairs available</option>";
      pairSelect.disabled = true;
      pairRows.innerHTML =
        '<tr><td colspan="4" class="status">No rate available for lookup.</td></tr>';
      pairStatus.textContent = "No currency pairs are available to look up.";
      pairStatus.classList.remove("err");
      return;
    }

    rows.innerHTML = rates
      .map(
        (rate) => `
      <tr>
        <td class="mono">${rate.base}</td>
        <td class="mono">${rate.quote}</td>
        <td class="mono">${rate.rate}</td>
        <td class="mono">${rate.rateDate}</td>
      </tr>`,
      )
      .join("");
    status.textContent = `${rates.length} latest rates loaded from the database.`;
    status.classList.remove("err");

    pairSelect.innerHTML = rates
      .map((rate) => {
        const pair = `${rate.base}/${rate.quote}`;
        return `<option value="${pair}">${pair}</option>`;
      })
      .join("");

    const eurUsd = rates.find(
      (rate) => rate.base === "EUR" && rate.quote === "USD",
    );
    if (eurUsd) {
      pairSelect.value = "EUR/USD";
    }

    pairSelect.addEventListener("change", lookupSelectedPair);
    await lookupSelectedPair();
  } catch (err) {
    rows.innerHTML =
      '<tr><td colspan="4" class="status err">Could not load rates.</td></tr>';
    status.textContent =
      "Is the app running and the database seeded? Try /api/health/db. (" +
      err.message +
      ")";
    status.classList.add("err");
    pairSelect.innerHTML = "<option>Could not load pairs</option>";
    pairSelect.disabled = true;
    pairRows.innerHTML =
      '<tr><td colspan="4" class="status err">Could not load pair lookup.</td></tr>';
    pairStatus.textContent =
      "Could not load currency pairs for lookup. (" + err.message + ")";
    pairStatus.classList.add("err");
  }
}

async function lookupSelectedPair() {
  const pairSelect = document.getElementById("pair-select");
  const pairRows = document.getElementById("pair-lookup-result");
  const pairStatus = document.getElementById("pair-status");

  if (pairSelect.disabled || !pairSelect.value) {
    return;
  }

  const [base, quote] = pairSelect.value.split("/");

  try {
    const res = await fetch(`/api/rates/${base}/${quote}`);
    const body = await res.json().catch(() => ({}));
    if (!res.ok) {
      throw new Error(body.error || "HTTP " + res.status);
    }

    pairRows.innerHTML = `
      <tr>
        <td class="mono">${body.base}</td>
        <td class="mono">${body.quote}</td>
        <td class="mono">${body.rate}</td>
        <td class="mono">${body.rateDate}</td>
      </tr>`;
    pairStatus.textContent = `Showing the latest rate for ${body.base}/${body.quote}.`;
    pairStatus.classList.remove("err");
  } catch (err) {
    pairRows.innerHTML =
      '<tr><td colspan="4" class="status err">Could not load that pair.</td></tr>';
    pairStatus.textContent = err.message;
    pairStatus.classList.add("err");
  }
}

loadRates();
