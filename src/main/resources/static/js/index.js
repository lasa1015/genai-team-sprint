async function loadRates() {
  const rows = document.getElementById('rate-rows');
  const status = document.getElementById('rates-status');

  try {
    const res = await fetch('/api/rates');
    if (!res.ok) throw new Error('HTTP ' + res.status);

    const rates = await res.json();
    if (rates.length === 0) {
      rows.innerHTML = '<tr><td colspan="4" class="status">No rates found.</td></tr>';
      status.textContent = 'No FX rates are available in the database.';
      status.classList.remove('err');
      return;
    }

    rows.innerHTML = rates.map(rate => `
      <tr>
        <td class="mono">${rate.base}</td>
        <td class="mono">${rate.quote}</td>
        <td class="mono">${rate.rate}</td>
        <td class="mono">${rate.rateDate}</td>
      </tr>`).join('');
    status.textContent = `${rates.length} latest rates loaded from the database.`;
    status.classList.remove('err');
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="4" class="status err">Could not load rates.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? Try /api/health/db. (' + err.message + ')';
    status.classList.add('err');
  }
}

loadRates();