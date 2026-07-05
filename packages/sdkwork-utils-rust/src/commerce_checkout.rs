//! Shared commerce checkout/cashier URL helpers for order and payment capabilities.

const DEFAULT_CASHIER_BASE_URL: &str = "https://im.sdkwork.com/cashier";

/// Resolve cashier base URL from `SDKWORK_COMMERCE_CASHIER_BASE_URL` or the platform default.
pub fn commerce_cashier_base_url() -> String {
    std::env::var("SDKWORK_COMMERCE_CASHIER_BASE_URL")
        .ok()
        .map(|value| value.trim().trim_end_matches('/').to_owned())
        .filter(|value| !value.is_empty())
        .unwrap_or_else(|| DEFAULT_CASHIER_BASE_URL.to_owned())
}

/// Map `commerce_order.subject` to a cashier scene query parameter.
pub fn commerce_cashier_scene(order_subject: Option<&str>) -> &'static str {
    match order_subject
        .map(str::trim)
        .filter(|value| !value.is_empty())
        .map(str::to_ascii_lowercase)
        .as_deref()
    {
        Some("points_recharge") => "recharge",
        Some("product") | Some("physical") => "checkout",
        Some("virtual_goods") | Some("membership") => "virtual",
        _ => "checkout",
    }
}

/// Build a cashier deep-link for owner-order payment (`orderId` uses business order number/sn).
pub fn build_commerce_cashier_url(scene: &str, order_id: &str, out_trade_no: &str) -> String {
    format!(
        "{}?scene={}&orderId={}&outTradeNo={}",
        commerce_cashier_base_url(),
        scene,
        order_id,
        out_trade_no
    )
}

/// Returns true when a normalized provider webhook status indicates payment success.
pub fn commerce_webhook_payment_status_is_success(status: Option<&str>) -> bool {
    matches!(
        status
            .map(str::trim)
            .filter(|value| !value.is_empty())
            .map(str::to_ascii_lowercase)
            .as_deref(),
        Some("succeeded" | "success" | "paid" | "trade_success" | "payment.succeeded")
    )
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn cashier_scene_maps_points_recharge() {
        assert_eq!(commerce_cashier_scene(Some("points_recharge")), "recharge");
    }

    #[test]
    fn build_cashier_url_includes_scene_and_ids() {
        let url = build_commerce_cashier_url("recharge", "ORD-1", "OT-1");
        assert!(url.contains("scene=recharge"));
        assert!(url.contains("orderId=ORD-1"));
        assert!(url.contains("outTradeNo=OT-1"));
    }

    #[test]
    fn alipay_trade_success_is_success() {
        assert!(commerce_webhook_payment_status_is_success(Some(
            "TRADE_SUCCESS"
        )));
    }
}
