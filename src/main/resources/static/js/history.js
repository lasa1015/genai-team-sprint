function formatExecutedAt(value) {
  return value.replace("T", " ");
}

async function loadHistory() {
  const rows = document.getElementById("history-rows");
  const status = document.getElementById("history-status");

  try {
    const res = await fetch("/api/transfers");
    if (!res.ok) throw new Error("HTTP " + res.status);

    const transfers = await res.json();
    if (transfers.length === 0) {
      rows.innerHTML =
        '<tr><td colspan="6" class="status">No transfers recorded yet.</td></tr>';
      status.textContent = "Run a conversion to create the first history row.";
      status.classList.remove("err");
      return;
    }

    rows.innerHTML = transfers
      .map(
        (transfer) => `
      <tr>
        <td class="mono">${transfer.fromAccount}</td>
        <td class="mono">${transfer.toAccount}</td>
        <td class="mono">${Number(transfer.amount).toFixed(2)}</td>
        <td class="mono">${transfer.currency}</td>
        <td class="mono">${formatExecutedAt(transfer.executedAt)}</td>
        <td>${transfer.status}</td>
      </tr>`,
      )
      .join("");
    status.textContent = `${transfers.length} transfers loaded, newest first.`;
    status.classList.remove("err");
  } catch (err) {
    rows.innerHTML =
      '<tr><td colspan="6" class="status err">Could not load transfers.</td></tr>';
    status.textContent =
      "Could not load transfer history. (" + err.message + ")";
    status.classList.add("err");
  }
}

loadHistory();
