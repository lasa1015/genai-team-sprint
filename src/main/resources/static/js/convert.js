function formatMoney(value) {
  return Number(value).toFixed(2);
}

function formatRate(value) {
  return Number(value).toFixed(4);
}

async function loadPairs() {
  const pairSelect = document.getElementById("convert-pair");
  const status = document.getElementById("convert-status");

  try {
    const res = await fetch("/api/rates");
    if (!res.ok) throw new Error("HTTP " + res.status);

    const rates = await res.json();
    if (rates.length === 0) {
      pairSelect.innerHTML = "<option>No pairs available</option>";
      pairSelect.disabled = true;
      status.textContent =
        "No currency pairs are available for conversion yet.";
      status.classList.remove("err");
      return;
    }

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

    status.textContent =
      "Latest rates loaded. Enter an amount to calculate the quote.";
    status.classList.remove("err");
  } catch (err) {
    pairSelect.innerHTML = "<option>Could not load pairs</option>";
    pairSelect.disabled = true;
    status.textContent = "Could not load currency pairs. (" + err.message + ")";
    status.classList.add("err");
  }
}

async function submitConversion(event) {
  event.preventDefault();

  const pairSelect = document.getElementById("convert-pair");
  const amountInput = document.getElementById("convert-amount");
  const status = document.getElementById("convert-status");
  const result = document.getElementById("convert-result");

  if (pairSelect.disabled) {
    return;
  }

  const [base, quoteCurrency] = pairSelect.value.split("/");
  const query = new URLSearchParams({
    base,
    quote: quoteCurrency,
    amount: amountInput.value.trim(),
  });

  try {
    const quoteRes = await fetch(`/api/convert?${query.toString()}`);
    const quote = await quoteRes.json().catch(() => ({}));
    if (!quoteRes.ok) {
      throw new Error(quote.error || "HTTP " + quoteRes.status);
    }

    const transferRes = await fetch("/api/transfers", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        base,
        quote: quoteCurrency,
        amount: amountInput.value.trim(),
      }),
    });
    const transfer = await transferRes.json().catch(() => ({}));
    if (!transferRes.ok) {
      throw new Error(transfer.error || "HTTP " + transferRes.status);
    }

    result.innerHTML = `
      <tr>
        <td class="mono">${formatMoney(quote.amount)} ${base}</td>
        <td class="mono">${formatRate(quote.rate)}</td>
        <td class="mono">${formatMoney(quote.converted)} ${quoteCurrency}</td>
        <td class="mono">${formatMoney(quote.fee)} ${base}</td>
        <td class="mono">${formatMoney(quote.total)} ${base}</td>
      </tr>`;
    status.textContent = `Converted ${formatMoney(quote.amount)} ${base} to ${formatMoney(quote.converted)} ${quoteCurrency} and recorded the transfer from account ${transfer.fromAccount} to ${transfer.toAccount}.`;
    status.classList.remove("err");
  } catch (err) {
    result.innerHTML =
      '<tr><td colspan="5" class="status err">No conversion available.</td></tr>';
    status.textContent = err.message;
    status.classList.add("err");
  }
}

loadPairs();
document
  .getElementById("convert-form")
  .addEventListener("submit", submitConversion);
